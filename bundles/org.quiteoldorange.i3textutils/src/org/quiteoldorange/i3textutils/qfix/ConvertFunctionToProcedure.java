package org.quiteoldorange.i3textutils.qfix;

import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;
import org.quiteoldorange.i3textutils.qfix.MethodConverter.ConversionDirection;
import org.quiteoldorange.i3textutils.qfix.quickactions.Messages;

import com._1c.g5.v8.dt.bsl.ui.quickfix.AbstractExternalQuickfixProvider;

public class ConvertFunctionToProcedure
    extends AbstractExternalQuickfixProvider
{

    public ConvertFunctionToProcedure()
    {
        // TODO Auto-generated constructor stub
    }

    @Fix("function-should-return-value")
    public void run(final Issue issue, IssueResolutionAcceptor acceptor)
    {
        acceptor.accept(issue, Messages.MethodQuickActions_ConvertToProcedure, "", null, //$NON-NLS-1$
            new MethodConverter(issue, ConversionDirection.ToProcedure));
    }
}
