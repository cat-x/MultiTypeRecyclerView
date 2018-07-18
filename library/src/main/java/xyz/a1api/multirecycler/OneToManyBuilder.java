/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.a1api.multirecycler;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import xyz.a1api.multirecycler.multi.ClassLinker;
import xyz.a1api.multirecycler.multi.ClassLinkerWrapper;
import xyz.a1api.multirecycler.multi.Linker;
import xyz.a1api.multirecycler.multi.OneToManyEndpoint;
import xyz.a1api.multirecycler.multi.OneToManyFlow;
import xyz.a1api.multirecycler.multi.Preconditions;

/**
 * @author drakeet
 */
class OneToManyBuilder<T> implements OneToManyFlow<T>, OneToManyEndpoint<T> {

    private final @NonNull
    BaseMultiAdapter adapter;
    private final @NonNull
    Class<? extends T> clazz;
    private Binder<T, ? extends BaseViewHolder>[] binders;


    OneToManyBuilder(@NonNull BaseMultiAdapter adapter, @NonNull Class<? extends T> clazz) {
        this.clazz = clazz;
        this.adapter = adapter;
    }


    @Override
    @CheckResult
    @SafeVarargs
    public final @NonNull
    OneToManyEndpoint to(@NonNull Binder<T, ? extends BaseViewHolder>... binders) {
        Preconditions.checkNotNull(binders);
        this.binders = binders;
        return this;
    }


    @Override
    public BaseMultiAdapter withLinker(@NonNull Linker<T> linker) {
        Preconditions.checkNotNull(linker);
        doRegister(linker);
        return adapter;
    }


    @Override
    public BaseMultiAdapter withClassLinker(@NonNull ClassLinker<T> classLinker) {
        Preconditions.checkNotNull(classLinker);
        doRegister(ClassLinkerWrapper.wrap(classLinker, binders));
        return adapter;
    }


    private void doRegister(@NonNull Linker<T> linker) {
        for (Binder<T, ? extends BaseViewHolder> binder : binders) {
            adapter.register(clazz, binder, linker);
        }
    }
}
