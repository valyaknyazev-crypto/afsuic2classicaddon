package com.afsu.mod.tools;

import java.util.List;
import net.minecraft.network.chat.Component;

public interface IDebuggableMachine {
    /**
     * Возвращает список диагностических компонентов о текущем внутреннем состоянии механизма.
     */
    List<Component> getDiagnostics();
}
