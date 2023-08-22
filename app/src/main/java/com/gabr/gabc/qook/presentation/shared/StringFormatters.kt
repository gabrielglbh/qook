package com.gabr.gabc.qook.presentation.shared

class StringFormatters {
    companion object {
        fun generateSubStrings(input: String): List<String> {
            val subStrings = mutableListOf<String>()
            var currentSubString = ""

            for (char in input) {
                currentSubString += char.lowercaseChar()
                subStrings.add(currentSubString)
            }

            return subStrings
        }
    }
}