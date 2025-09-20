package com.LetMeDoWith.LetMeDoWith.batch.item;

import com.LetMeDoWith.LetMeDoWith.batch.dto.DowithTaskDto;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class DowithTaskItemReader implements ItemReader<DowithTaskDto> {

    @Override
    public DowithTaskDto read() throws Exception {
        return null;
    }
}
