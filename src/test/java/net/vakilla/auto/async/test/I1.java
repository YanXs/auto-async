package net.vakilla.auto.async.test;

import net.vakilla.auto.async.Asyncable;
import net.vakilla.auto.async.AutoAsync;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Xs
 */
@AutoAsync(generateFacade = true)
public interface I1 {

    Param get();

    @Asyncable
    List<Param> getParams(Integer id);

    @Asyncable
    String getValue(Map<String, Param> paramMap);

    @Asyncable
    int testInt();
}
