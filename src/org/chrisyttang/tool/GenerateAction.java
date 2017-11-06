package org.chrisyttang.tool;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

public class GenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        GenerateDialog dig = new GenerateDialog(psiClass);
        dig.show();
        if(dig.isOK()){
            generateCompareTo(psiClass, dig.getFields());
        }
    }

    private void generateCompareTo(PsiClass psiClass, List<PsiField> fields) {
        RunResult execute = new WriteCommandAction.Simple(psiClass.getProject(),psiClass.getContainingFile()) {


            @Override
            protected void run() throws Throwable {
                StringBuilder builder = new StringBuilder("public int compareTo(");

                builder.append(psiClass.getName()).append(" that){\n");
                builder.append("return com.google.common.collect.ComparisonChain.start()");

                for (PsiField field:fields){
                    builder.append(".compare(this.").append(field.getName()).append(", that.");
                    builder.append(field.getName()).append(")");
                }


                builder.append(".result();\n}");

                // 按照文本构建代码 应该可以使用latex的工具 可以具体查找一下 有工具来实现
                PsiMethod compareTo = JavaPsiFacade.getElementFactory(getProject()).createMethodFromText(builder.toString(),psiClass);
                psiClass.add(compareTo);

                PsiElement method = psiClass.add(compareTo);

                // 这里加入一个code style工具可以帮助我们 建立更好的code style
                // 这里对compareto方法进行 代码格式优化
                JavaCodeStyleManager.getInstance(getProject()).shortenClassReferences(method);


            }
        }.execute();


    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }


    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psilFile =   e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psilFile == null||editor == null)
        {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psilFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);

        if (psiClass == null){
            return null;
        }

        return psiClass;
    }
}
