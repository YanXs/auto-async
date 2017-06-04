package net.vakilla.auto.async.test;

import com.google.common.util.concurrent.ListenableFuture;
import net.vakilla.auto.async.Asyncable;
import net.vakilla.auto.async.AutoAsync;

import java.util.concurrent.Future;

/**
 * @author Xs
 */
@AutoAsync
public interface I2 {

    Future testFuture0();

    @Asyncable
    ListenableFuture testFuture1();
}
