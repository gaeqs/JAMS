/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
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

package net.jamsimulator.jams.mips.register;

public class COP0RegistersBits {

    public static final int STATUS_CU = 29;
    public static final int STATUS_RW = 28;
    public static final int STATUS_FR = 26;
    public static final int STATUS_MX = 24;
    public static final int STATUS_BEV = 22;
    public static final int STATUS_SR = 20;
    public static final int STATUS_NMI = 19;
    public static final int STATUS_ASE = 18;
    public static final int STATUS_IMPL = 16;
    public static final int STATUS_IM2 = 10;
    public static final int STATUS_IPL = 10;
    public static final int STATUS_IM0 = 8;
    public static final int STATUS_KSU = 3;
    public static final int STATUS_UM = 4;
    public static final int STATUS_ERL = 2;
    public static final int STATUS_EXL = 1;
    public static final int STATUS_IE = 0;

    public static final int INT_CTL_VS = 5;

    public static final int SRS_CTL_HSS = 26;
    public static final int SRS_CTL_EICSS = 18;
    public static final int SRS_CTL_ESS = 12;
    public static final int SRS_CTL_PSS = 6;
    public static final int SRS_CTL_CSS = 0;

    public static final int CAUSE_BD = 31;
    public static final int CAUSE_TI = 30;
    public static final int CAUSE_CE = 28;
    public static final int CAUSE_DC = 27;
    public static final int CAUSE_PCI = 26;
    public static final int CAUSE_ASE_1 = 24;
    public static final int CAUSE_ASE_2 = 16;
    public static final int CAUSE_IV = 23;
    public static final int CAUSE_WP = 22;
    public static final int CAUSE_FDCI = 21;
    public static final int CAUSE_IP = 8;
    public static final int CAUSE_RIPL = 10;
    public static final int CAUSE_EX_CODE = 2;

}
