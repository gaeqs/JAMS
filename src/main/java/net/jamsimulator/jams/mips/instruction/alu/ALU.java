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

package net.jamsimulator.jams.mips.instruction.alu;

import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONObject;

import java.util.Map;

public record ALU(ALUType type, int cyclesRequired) {

    public static ALU fromJSON(String json) {
        return fromJSON(new JSONObject(json));
    }

    public static ALU fromJSON(JSONObject json) {
        return new ALU(
                Manager.of(ALUType.class).get(json.getString("type")).orElseThrow(),
                json.getInt("cyclesRequired")
        );
    }

    public ALU {
        Validate.notNull(type, "Type cannot be null!");
    }

    public JSONObject toJSON() {
        return new JSONObject(Map.of(
                "type", type.getName(),
                "cyclesRequired", cyclesRequired)
        );
    }
}
