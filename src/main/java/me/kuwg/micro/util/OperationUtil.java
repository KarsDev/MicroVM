package me.kuwg.micro.util;

public final class OperationUtil {

    public static Object add(final Object leftObj, final Object rightObj) {
        if (leftObj instanceof String || rightObj instanceof String) {
            final String left = String.valueOf(leftObj);
            final String right = String.valueOf(rightObj);
            return left + right;
        }

        if (!(rightObj instanceof final Number right && leftObj instanceof final Number left)) {
            throw new IllegalArgumentException("Expected string or number in adding.");
        }

        if (leftObj instanceof Double || rightObj instanceof Double) {
            return left.doubleValue() + right.doubleValue();
        }

        if (leftObj instanceof Integer && rightObj instanceof Integer) {
            return left.intValue() + right.intValue();
        }

        if (leftObj instanceof Byte && rightObj instanceof Byte) {
            return (byte) left + (byte) right;
        }

        return left.longValue() + right.longValue();
    }

    public static Object sub(final Object leftObj, final Object rightObj) {
        if (!(rightObj instanceof final Number right && leftObj instanceof final Number left)) {
            throw new IllegalArgumentException("Expected number in subtraction.");
        }

        if (leftObj instanceof Double || rightObj instanceof Double) {
            return left.doubleValue() - right.doubleValue();
        }

        if (leftObj instanceof Integer && rightObj instanceof Integer) {
            return left.intValue() - right.intValue();
        }

        if (leftObj instanceof Byte && rightObj instanceof Byte) {
            return (byte) (left.byteValue() - right.byteValue());
        }

        return left.longValue() - right.longValue();
    }

    public static Object mul(final Object leftObj, final Object rightObj) {
        if (!(rightObj instanceof final Number right && leftObj instanceof final Number left)) {
            throw new IllegalArgumentException("Expected number in multiplication.");
        }

        if (leftObj instanceof Double || rightObj instanceof Double) {
            return left.doubleValue() * right.doubleValue();
        }

        if (leftObj instanceof Integer && rightObj instanceof Integer) {
            return left.intValue() * right.intValue();
        }

        if (leftObj instanceof Byte && rightObj instanceof Byte) {
            return (byte) (left.byteValue() * right.byteValue());
        }

        return left.longValue() * right.longValue();
    }

    public static Object div(final Object leftObj, final Object rightObj) {
        if (!(rightObj instanceof final Number right && leftObj instanceof final Number left)) {
            throw new IllegalArgumentException("Expected number in division.");
        }

        if (rightObj instanceof Integer && (Integer) rightObj == 0) {
            throw new ArithmeticException("Division by zero.");
        }

        if (rightObj instanceof Double && (Double) rightObj == 0.0) {
            throw new ArithmeticException("Division by zero.");
        }

        if (leftObj instanceof Double || rightObj instanceof Double) {
            return left.doubleValue() / right.doubleValue();
        }

        if (leftObj instanceof Integer && rightObj instanceof Integer) {
            return left.intValue() / right.intValue();
        }

        if (leftObj instanceof Byte && rightObj instanceof Byte) {
            return (byte) (left.byteValue() / right.byteValue());
        }

        return left.longValue() / right.longValue();
    }
}