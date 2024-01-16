package antifraud;

public class LuhnCheck {
    static boolean cardNumValidation(String number) {
        return number.length() == 16 && number.matches("^[0-9]+$");
    }
    static boolean isValidLuhn(String number) {
        int checksum = Character.getNumericValue(number.charAt(number.length() - 1));
        int total = 0;

        for (int i = number.length() - 2; i >= 0; i--) {
            int sum = 0;
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
            }
            sum = digit / 10 + digit % 10;
            total += sum;
        }
        return 10 - total % 10 == checksum;
    }
}
