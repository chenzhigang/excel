package org.czg.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author czg
 */
@Slf4j
@Service
public class ImportService<T> implements Consumer<T> {

    @Override
    public void accept(T t) {
        if (t instanceof List) {
            log.info("批量导入多条数据业务处理：{}", t);
        } else {
            log.info("批量导入单条数据业务处理：{}", t);
        }
    }

}
