package com.day.mctokook.utils.arg;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.day.mctokook.utils.arg.getter.CustomIntGetter;
import com.day.mctokook.utils.arg.getter.CustomStrGetter;

import cn.hutool.core.util.StrUtil;
import snw.jkook.message.Message;

public class ArgumentsProcessor {

    private final Message senderMessage;
    private final Object[] arguments;
    private boolean success = true;
    private final Logger logger;
    private final boolean useLog;
    private int size = 0;
    private final List<Integer> sizeCases = new ArrayList<>();
    private final List<Integer> indexCases = new ArrayList<>();
    private String INDEX_NOT_FOUND_MESSAGE = "当前索引:{} 未找到";

    public ArgumentsProcessor(Object[] arguments, @NotNull Message message, Logger logger, Integer size) {
        this.senderMessage = message;
        this.logger = logger;
        useLog = logger != null;
        if (arguments == null) arguments = new Object[0];
        this.arguments = arguments;
    }

    public ArgumentsProcessor setIndexNotFoundMessage(String message) {
        this.INDEX_NOT_FOUND_MESSAGE = message;
        return this;
    }

    public ArgumentsProcessor size(final int size, String errorMessage) {
        this.size = size;
        if (arguments.length != size) {
            replay(errorMessage);
            log("argument index out of bounds");
        }
        return this;
    }

    public int getAsInt(CustomIntGetter getter) {
        return getter.getAsInt(arguments);
    }

    public int getAsInt(String errorMessage) {
        int res = -1;
        if (checkIndex()) return res;
        int index = getIndex();
        try {
            res = Integer.parseInt((String) arguments[index]);
        } catch (NumberFormatException e) {
            success = false;
            replay(errorMessage);
            log(StrUtil.format("argument index {},format to number error ,message:{}", index, e.getLocalizedMessage()));
            return res;
        }
        sizeCases.clear();
        indexCases.clear();
        return res;
    }

    public String getAsString(String errorMessage) {
        String res = null;
        if (checkIndex()) return res;
        int index = getIndex();
        try {
            res = (String) arguments[index];
        } catch (ClassCastException e) {
            success = false;
            replay(errorMessage);
            log(StrUtil.format("argument index {},format to string error ,message:{}", index, e.getLocalizedMessage()));
        }
        sizeCases.clear();
        indexCases.clear();
        return res;
    }

    public String getAsString(CustomStrGetter getter) {
        return getter.getAsStr(arguments);
    }

    public ArgumentsProcessor when(int size, int index) {
        sizeCases.add(size);
        indexCases.add(index);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    private void log(String message) {
        if (useLog) {
            logger.info(message);
        }
    }

    private void replay(String message) {
        if (success) senderMessage.reply(message);
    }

    private boolean checkIndex() {
        boolean res = false;
        for (Integer poss : sizeCases) {
            if (arguments.length == poss) {
                res = true;
                break;
            }
        }
        if (!res) {
            success = false;
            log("index check failed");
            replay("没有找到与其对应的参数大小匹配");
            return true;
        }
        return false;
    }

    private int getIndex() {
        for (int i = 0; i < sizeCases.size(); i++) {
            if (arguments.length == sizeCases.get(i)) {
                return indexCases.get(i);
            }
        }
        return -1;
    }

    private List<Integer> indexOf(int index) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < sizeCases.size(); i++) {
            Integer num = indexCases.get(i);
            if (num.equals(index)) {
                res.add(i);
            }
        }
        return res;
    }
}
