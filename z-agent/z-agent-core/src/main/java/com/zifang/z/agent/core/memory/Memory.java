package com.zifang.z.agent.core.memory;
import com.zifang.z.agent.core.model.define.ModelMessage;

import java.util.*;

public class Memory {
    private final List<ModelMessage> history = new ArrayList<>();
    private static final int MAX_HISTORY = 10;

    public void add(ModelMessage msg) {
        history.add(msg);
        if (history.size() > MAX_HISTORY) history.remove(0);
    }

    public List<ModelMessage> getHistory() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }

}