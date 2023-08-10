/**
 *
 */
package org.quiteoldorange.i3textutils.bsl.parser.expressions;

import java.util.LinkedList;
import java.util.List;

import org.quiteoldorange.i3textutils.Tuple;
import org.quiteoldorange.i3textutils.bsl.lexer.Lexer;
import org.quiteoldorange.i3textutils.bsl.lexer.Token;
import org.quiteoldorange.i3textutils.bsl.parser.AbsractBSLElementNode;
import org.quiteoldorange.i3textutils.bsl.parser.BSLParsingException;
import org.quiteoldorange.i3textutils.bsl.parser.BSLParsingException.ExpectedClosingBracket;
import org.quiteoldorange.i3textutils.bsl.parser.ConstantNode;
import org.quiteoldorange.i3textutils.bsl.parser.IdentifierNode;
import org.quiteoldorange.i3textutils.bsl.parser.expressions.OperationNode.Operator;

import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;

/**
 * @author ozolotarev
 *
 */
public class ExpressionNode
    extends AbsractBSLElementNode
{
    @Override
    public String serialize(ScriptVariant scriptVariant)
    {
        StringBuilder builder = new StringBuilder();

        for (AbsractBSLElementNode node : getChildren())
        {
            // TODO: проверка на необходимость лепить перенос
            String nodeValue = node.serialize(scriptVariant);
            builder.append(nodeValue);

            if (getChildren().indexOf(node) != getChildren().size() - 1)
                builder.append(", "); //$NON-NLS-1$
        }

        if (mCompound)
            return "(" + builder.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        else
            return builder.toString();
    }

    @SuppressWarnings("unused")
    private boolean mCompound;

    public AbsractBSLElementNode ParseExpressionNode(Lexer stream, Token.Type endingToken) throws BSLParsingException
    {
        while (true)
        {
            Token t = readTokenTracked(stream);

            if (t == null)
                break;

            if (t.getType() == endingToken)
                break;

            switch (t.getType())
            {
            case Identifier:
                addChildren(new IdentifierNode(stream));
                break;
            case NumericConstant:
            case StringConstant:
            case DateConstant:
            case BooleanConst:
                addChildren(new ConstantNode(stream));
                break;
            case PlusSign:
                addChildren(new OperationNode(stream, Operator.Addition));
                break;
            case MinusSign:
                addChildren(new OperationNode(stream, Operator.Substraction));
                break;
            case MultiplicationSign:
                addChildren(new OperationNode(stream, Operator.Multiplication));
                break;
            case DivisionSign:
                addChildren(new OperationNode(stream, Operator.Division));
                break;
            case ModuloSign:
                addChildren(new OperationNode(stream, Operator.Modulo));
                break;
            case KeywordOr:
                addChildren(new OperationNode(stream, Operator.LogicalOr));
                break;
            case KeywordAnd:
                addChildren(new OperationNode(stream, Operator.LogicalAnd));
                break;
            case KeywordNot:
                addChildren(new OperationNode(stream, Operator.LogicalNot));
                break;
            case OpeningBracket:
                addChildren(new ExpressionOpeningBracket(stream, 0));
                break;
            case ClosingBracket:
                addChildren(new ExpressionClosingBracket(stream, 0));
                break;
            case Dot:
                addChildren(new MemberAccessNode(stream));
                break;
            case Comma:
                addChildren(new MultipleExpressionsNode(stream));
                break;
            case EqualsSign:

                // TODO: проверить на => и аналогично

            default:
                throw new BSLParsingException.UnexpectedToken(stream, t);
            }

        }

        return null;
    }

    public ExpressionNode(Lexer stream, Token.Type endToken) throws BSLParsingException
    {
        super(null);

        ParseExpressionNode(stream, endToken);
        reduce(getChildren());

        int a = 1;
    }

    @SuppressWarnings("unused")
    private Tuple<Integer, ExpressionNode> parseBracketsExpression(AbsractBSLElementNode node,
        List<AbsractBSLElementNode> children,
        Class<?> openingBracket, Class<?> closingBracket) throws BSLParsingException
    {
        boolean reverse = node instanceof ExpressionClosingBracket;

        AbsractBSLElementNode endingNode =
            findEndingBracket(node, reverse, children, openingBracket, closingBracket);

        int startIndex = 0;
        int endIndex = 0;

        if (reverse)
        {
            startIndex = children.indexOf(endingNode);
            endIndex = children.indexOf(node) + 1;
        }
        else
        {
            startIndex = children.indexOf(node);
            endIndex = children.indexOf(endingNode) + 1;
        }

        var slice = children.subList(startIndex, endIndex);
        ExpressionNode compoundExpression = new ExpressionNode(slice.subList(1, slice.size() - 1), true);
        slice.clear();

        //children.add(startIndex, compoundExpression);

        return new Tuple<>(startIndex, compoundExpression);
    }

    /**
     * @throws
     * @throws ExpectedClosingBracket
     */
    private void reduce(List<AbsractBSLElementNode> children) throws BSLParsingException
    {
        while (true)
        {
            if (children.size() < 2)
                break;

            AbsractBSLElementNode node = findMostPrecdenceOperator(children);

            if (node == null)
                break;

            if (node instanceof ExpressionOpeningBracket || node instanceof ExpressionClosingBracket)
            {
                Tuple<Integer, ExpressionNode> compoundExpression =
                    parseBracketsExpression(node, children, ExpressionOpeningBracket.class,
                    ExpressionClosingBracket.class);

                int startIndex = compoundExpression.getFirst();
                ExpressionNode expression = compoundExpression.getSecond();

                if (startIndex > 0)
                {
                   var prevNode = children.get(startIndex - 1);

                   if (prevNode instanceof IdentifierNode)
                   {
                       children.remove(startIndex - 1);
                       children.add(startIndex - 1, new MethodCallNode((IdentifierNode)prevNode, expression));
                   }

                }
                else
                    children.add(startIndex, expression);

            }
            else if (node instanceof MemberAccessNode)
            {
                MemberAccessNode memberNode = (MemberAccessNode)node;
                int nodeIndex = children.indexOf(node);

                if (nodeIndex == 0)
                    throw new BSLParsingException.UnexpectedMemberRead();

                if (nodeIndex == children.size() - 1)
                    throw new BSLParsingException.UnexpectedMemberRead();

                var leftNode = children.get(nodeIndex - 1);
                var rightNode = children.get(nodeIndex + 1);

                // TODO: MemberAccessExpression

                var memberAcessExpression = new MemberAccessExpression(null);

                memberAcessExpression.setLeftNode(leftNode);
                memberAcessExpression.setRightNode(rightNode);

                children.remove(nodeIndex + 1);
                children.remove(nodeIndex);
                children.remove(nodeIndex - 1);

                children.add(nodeIndex - 1, memberAcessExpression);

            }
            else if (node instanceof OperationNode)
            {
                OperationNode operator = (OperationNode)node;

                int startIndex = children.indexOf(node);

                var sliceLeft = children.subList(0, startIndex);
                var sliceRight = children.subList(startIndex + 1, children.size());

                operator.setLeftNode(new ExpressionNode(sliceLeft));
                operator.setRightNode(new ExpressionNode(sliceRight));

                operator.toString();
                children.clear();
                children.add(operator);
            }

        }
    }

    @Override
    public String toString()
    {
        if (getChildren().size() == 0)
            return "<Bad expression>"; //$NON-NLS-1$

        String result = ""; //$NON-NLS-1$

        for (var item : getChildren())
        {
            result += item.toString() + ",";
        }

        //if (mCompound)
        //  result = "(" + result + ")"; //$NON-NLS-1$ //$NON-NLS-2$

        if (getChildren().size() > 1)
            return "{" + result.substring(0, result.length() - 1) + "}";
        else
            return result.substring(0, result.length() - 1);
    }

    private List<List<AbsractBSLElementNode>> splitMultipleExpressions(List<AbsractBSLElementNode> input)
    {
        List<List<AbsractBSLElementNode>> result = new LinkedList<>();

        List<AbsractBSLElementNode> temp = new LinkedList<>();

        var iterator = input.iterator();

        while (iterator.hasNext())
        {
            var node = iterator.next();

            if (node instanceof MultipleExpressionsNode)
            {
                result.add(temp);
                temp = new LinkedList<>();
                continue;
            }

            temp.add(node);
        }

        if (temp.size() > 0)
            result.add(temp);

        return result;
    }

    /**
     * @param slice
     * @throws ExpectedClosingBracket
     */
    public ExpressionNode(List<AbsractBSLElementNode> slice) throws BSLParsingException
    {
        super(null);

        var expressions = splitMultipleExpressions(slice);

        getChildren().clear();

        for (var expressionList : expressions)
        {
            reduce(expressionList);
            assert (expressionList.size() == 1);
            addChildren(expressionList.get(0));
        }

        int a = 1;
    }

    /**
     * @param subList
     * @param b
     * @throws BSLParsingException
     */
    public ExpressionNode(List<AbsractBSLElementNode> subList, boolean compound) throws BSLParsingException
    {

        this(subList);

        mCompound = compound;
    }

    /**
     * @param node
     * @param children
     * @return
     * @throws ExpectedClosingBracket
     */
    private AbsractBSLElementNode findEndingBracket(AbsractBSLElementNode startNode, boolean reverse,
        List<AbsractBSLElementNode> children, Class<?> openingBracketType, Class<?> endingBracketType)
        throws ExpectedClosingBracket
    {
        int level = 0;

        //  + (reverse ? -1 : 1)
        var iterator = children.listIterator(children.indexOf(startNode));

        var endClass = (reverse ? openingBracketType : endingBracketType);
        var openingClass = (reverse ? endingBracketType : openingBracketType);

        while (reverse ? iterator.hasPrevious() : iterator.hasNext())
        {
            AbsractBSLElementNode node = reverse ? iterator.previous() : iterator.next();

            if (node.getClass() == endClass && level == 0)
                return node;
            else if (node.getClass() == endClass && level != 0)
                level--;

            if (node.getClass() == openingClass)
                level++;
        }

        throw new BSLParsingException.ExpectedClosingBracket();

    }

    public AbsractBSLElementNode findMostPrecdenceOperator(List<AbsractBSLElementNode> nodes)
    {
        AbsractBSLElementNode best = null;
        int bestPrecedence = 99999;

        var iterator = nodes.listIterator(nodes.size());

        while(iterator.hasPrevious())
        {
            var item = iterator.previous();

            if (item instanceof IOperationNode)
            {
                IOperationNode opNode = (IOperationNode)item;

                if (opNode.precedence() < bestPrecedence)
                {
                    bestPrecedence = opNode.precedence();
                    best = item;
                }
            }
        }

        return best;
    }

}
