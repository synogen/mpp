package org.mppsolartest.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class IntegerListResponseCommand extends Command<List<Integer>> {

    @Override
    public List<Integer> parseResponse(String response) {;
        if (response.isEmpty() || response.equalsIgnoreCase("(NAK")) return new ArrayList<>(); //in case of empty or (NAK response
        var rParts = response.substring(1).split(" ");
        return Arrays.stream(rParts).map(Integer::valueOf).toList();
    }
}
