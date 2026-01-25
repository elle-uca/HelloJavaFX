package org.ln.hellofx;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts numbers between bases (2–36) while logging each intermediate calculation.
 * The utility is intended for rename preview workflows and does not touch the filesystem.
 *
 * @author Luca Noale
 */
public class NumberConverter {

    private static final Logger logger = LoggerFactory.getLogger(NumberConverter.class);

    /** Symbols usable for bases up to 36 */
    private static final String DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Converts a number from one base to another.
     */
    public String convert(String number, int baseIn, int baseOut) {

        // ---- Input validation ----
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Number must not be null or blank");
        }

        number = number.trim().toUpperCase();

        if (baseIn < 2 || baseIn > 36) {
            throw new IllegalArgumentException("baseIn must be between 2 and 36");
        }

        if (baseOut < 2 || baseOut > 36) {
            throw new IllegalArgumentException("baseOut must be between 2 and 36");
        }

        if (baseIn == baseOut) {
            return number;
        }

        logger.info("\n--- PASSO 1: Da Base {} a Base 10 ---", baseIn);
        long decimalValue = toDecimal(number, baseIn);
        logger.info("\nRisultato in Base 10: {}", decimalValue);

        if (baseOut == 10) {
            return String.valueOf(decimalValue);
        }

        logger.info("\n--- PASSO 2: Da Base 10 a Base {} ---", baseOut);
        String result = fromDecimal(decimalValue, baseOut);
        logger.info("\nRisultato Finale in Base {}: {}", baseOut, result);

        return result;
    }

    /**
     * Converts a number from any base (2–36) to decimal.
     */
    private long toDecimal(String number, int base) {
        long decimalValue = 0;

        logger.info("  Calcolo: Somma dei (Simbolo * Base ^ Posizione)");

        for (int i = 0; i < number.length(); i++) {
            char digitChar = number.charAt(i);
            int digitValue = DIGITS.indexOf(digitChar);

            if (digitValue < 0 || digitValue >= base) {
                throw new IllegalArgumentException(
                        "Invalid digit '" + digitChar + "' for base " + base);
            }

            int power = number.length() - 1 - i;
            long contribution = digitValue * (long) Math.pow(base, power);
            decimalValue += contribution;

            logger.info("  Posizione {}: {} ({}) * {}^{} = {}",
                    power, digitChar, digitValue, base, power, contribution);
        }

        return decimalValue;
    }

    /**
     * Converts a decimal number to the requested base (2–36).
     */
    private String fromDecimal(long decimalValue, int base) {
        if (decimalValue == 0) return "0";

        StringBuilder result = new StringBuilder();
        long currentNumber = decimalValue;

        logger.info("  Calcolo: Divisioni successive per la Base {}", base);

        while (currentNumber > 0) {
            long remainder = currentNumber % base;
            long quotient = currentNumber / base;

            char digitChar = DIGITS.charAt((int) remainder);
            result.insert(0, digitChar);

            logger.info("  {} / {} = Quoziente {}, Resto {} ({})",
                    currentNumber, base, quotient, remainder, digitChar);

            currentNumber = quotient;
        }

        return result.toString();
    }

    public static void main(String[] args) {
        NumberConverter converter = new NumberConverter();
        Scanner scanner = new Scanner(System.in);

        logger.info("--- Convertitore di Base Numerica (2–36) ---");

        logger.info("Inserisci il numero da convertire:");
        String number = scanner.nextLine();

        logger.info("Inserisci la base di partenza:");
        int baseIn = scanner.nextInt();

        logger.info("Inserisci la base di arrivo:");
        int baseOut = scanner.nextInt();

        scanner.close();

        converter.convert(number, baseIn, baseOut);
    }
}
