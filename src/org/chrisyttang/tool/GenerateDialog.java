package org.chrisyttang.tool;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GenerateDialog extends DialogWrapper {
    private CollectionListModel<PsiField> myFields;
    private final LabeledComponent myComponent;

    protected GenerateDialog(PsiClass psiClass) {
        super(psiClass.getProject());//psiClass 表示当前的类

        setTitle("Select Fields for ComparisonChain");


        myFields = new CollectionListModel<PsiField>(psiClass.getAllFields());//获取到 当前editor的class的所有类

        JList fieldList = new JList(myFields);

        fieldList.setCellRenderer(new DefaultPsiElementCellRenderer());


        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);

        decorator.disableAddAction();

        decorator.createPanel();

        JPanel panel = decorator.createPanel();

        myComponent = LabeledComponent.create(panel, "Fields to inclure in CompareTo()");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myComponent;
    }

    public List<PsiField> getFields() {
        return myFields.getItems();
    }
}
