/*
 *  MIT License
 *
 *  Copyright (c) 2024 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.instruction.basic.defaults

enum class R5CCondCondition(
    val mnemonic: String,
) {
    FALSE("f"),
    UNORDERED("un"),
    EQUAL("eq"),
    UNORDERED_OR_EQUAL("ueq"),
    ORDERED_OR_LESS_THAN("olt"),
    UNORDERED_OR_LESS_THAN("ult"),
    ORDERED_OR_LESS_THAN_OR_EQUAL("ole"),
    UNORDERED_OR_LESS_THAN_OR_EQUAL("ule"),
    SIGNALING_FALSE("sf"),
    NOT_GREATER_THAN_OR_LESS_THAN_OR_EQUAL("ngle"),
    SIGNALING_EQUAL("seq"),
    NOT_GREATER_THAN_OR_LESS_THAN("ngl"),
    LESS_THAN("lt"),
    NOT_GREATER_THAN_OR_EQUAL("nge"),
    LESS_THAN_OR_EQUAL("le"),
    NOT_GREATER_THAN("ngt");

    val unordered get() = (ordinal and 0b0001) > 0
    val equal get() = (ordinal and 0b0010) > 0
    val less get() = (ordinal and 0b0100) > 0
    val signal get() = (ordinal and 0b1000) > 0
}