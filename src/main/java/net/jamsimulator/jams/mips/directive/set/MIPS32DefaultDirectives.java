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

package net.jamsimulator.jams.mips.directive.set;

import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.defaults.*;

import java.util.HashSet;
import java.util.Set;

class MIPS32DefaultDirectives {

    static Set<Directive> directives = new HashSet<>();

    static {
        directives.add(new DirectiveAlign());
        directives.add(new DirectiveAscii());
        directives.add(new DirectiveAsciiz());
        directives.add(new DirectiveByte());
        directives.add(new DirectiveData());
        directives.add(new DirectiveDouble());
        directives.add(new DirectiveDWord());
        directives.add(new DirectiveEndmacro());
        directives.add(new DirectiveEqv());
        directives.add(new DirectiveErr());
        directives.add(new DirectiveExtern());
        directives.add(new DirectiveFloat());
        directives.add(new DirectiveFloat());
        directives.add(new DirectiveGlobl());
        directives.add(new DirectiveHalf());
        directives.add(new DirectiveInclude());
        directives.add(new DirectiveKData());
        directives.add(new DirectiveKText());
        directives.add(new DirectiveLab());
        directives.add(new DirectiveMacro());
        directives.add(new DirectiveSpace());
        directives.add(new DirectiveText());
        directives.add(new DirectiveWord());
    }

}
