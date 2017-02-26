package net.pilif0.open_desert.util;

/**
 * Enumeration of different severities for use with logs
 *
 * @author Filip Smola
 * @version 1.0
 */
public enum Severity {

    INFO {
        @Override
        public String toString() {
            return "[INFO]";
        }
    },

    WARNING {
        @Override
        public String toString() {
            return "[WARNING]";
        }
    },

    ERROR {
        @Override
        public String toString() {
            return "[ERROR]";
        }
    },

    SEVERE {
        @Override
        public String toString() {
            return "[SEVERE]";
        }
    },

    EXCEPTION {
        @Override
        public String toString() {
            return "[EXCEPTION]";
        }
    }
}
