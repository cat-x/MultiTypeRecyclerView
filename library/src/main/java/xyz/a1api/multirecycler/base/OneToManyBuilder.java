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

package xyz.a1api.multirecycler.base;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import xyz.a1api.multirecycler.base.multi.ClassLinker;
import xyz.a1api.multirecycler.base.multi.ClassLinkerWrapper;
import xyz.a1api.multirecycler.base.multi.Linker;
import xyz.a1api.multirecycler.base.multi.OneToManyEndpoint;
import xyz.a1api.multirecycler.base.multi.OneToManyFlow;

import static xyz.a1api.multirecycler.base.multi.Preconditions.checkNotNull;

/**
 * @author drakeet
 */
class OneToManyBuilder<T, VH extends BaseViewHolder> implements OneToManyFlow<T, VH>, OneToManyEndpoint<T, VH> {

    private final @NonNull
    BaseQuickAdapter adapter;
    private final @NonNull
    Class<? extends T> clazz;
    private Binder<T, VH>[] binders;


    OneToManyBuilder(@NonNull BaseQuickAdapter adapter, @NonNull Class<? extends T> clazz) {
        this.clazz = clazz;
        this.adapter = adapter;
    }


    @Override
    @CheckResult
    @SafeVarargs
    public final @NonNull
    OneToManyEndpoint to(@NonNull Binder<T, VH>... binders) {
        checkNotNull(binders);
        this.binders = binders;
        return this;
    }


    @Override
    public void withLinker(@NonNull Linker<T> linker) {
        checkNotNull(linker);
        doRegister(linker);
    }


    @Override
    public void withClassLinker(@NonNull ClassLinker<T, VH> classLinker) {
        checkNotNull(classLinker);
        doRegister(ClassLinkerWrapper.wrap(classLinker, binders));
    }


    private void doRegister(@NonNull Linker<T> linker) {
        for (Binder<T, VH> binder : binders) {
            adapter.register(clazz, binder, linker);
        }
    }
}
