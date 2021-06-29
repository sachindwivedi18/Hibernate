// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.collect;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.function.DoubleConsumer;
import java.util.function.LongConsumer;
import java.util.PrimitiveIterator;
import java.util.function.IntConsumer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import rp.com.google.common.base.Preconditions;
import java.util.function.BiFunction;
import rp.com.google.common.math.LongMath;
import java.util.Spliterator;
import java.util.stream.DoubleStream;
import java.util.OptionalDouble;
import java.util.stream.LongStream;
import java.util.OptionalLong;
import java.util.stream.IntStream;
import java.util.OptionalInt;
import rp.com.google.common.base.Optional;
import java.util.Spliterators;
import java.util.Iterator;
import java.util.stream.StreamSupport;
import java.util.Collection;
import java.util.stream.Stream;
import rp.com.google.common.annotations.GwtCompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class Streams
{
    public static <T> Stream<T> stream(final Iterable<T> iterable) {
        return (iterable instanceof Collection) ? ((Collection)iterable).stream() : StreamSupport.stream(iterable.spliterator(), false);
    }
    
    @Deprecated
    public static <T> Stream<T> stream(final Collection<T> collection) {
        return collection.stream();
    }
    
    public static <T> Stream<T> stream(final Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<? extends T>)iterator, 0), false);
    }
    
    public static <T> Stream<T> stream(final Optional<T> optional) {
        return optional.isPresent() ? Stream.of(optional.get()) : Stream.of((T[])new Object[0]);
    }
    
    public static <T> Stream<T> stream(final java.util.Optional<T> optional) {
        return optional.isPresent() ? Stream.of(optional.get()) : Stream.of((T[])new Object[0]);
    }
    
    public static IntStream stream(final OptionalInt optional) {
        return optional.isPresent() ? IntStream.of(optional.getAsInt()) : IntStream.empty();
    }
    
    public static LongStream stream(final OptionalLong optional) {
        return optional.isPresent() ? LongStream.of(optional.getAsLong()) : LongStream.empty();
    }
    
    public static DoubleStream stream(final OptionalDouble optional) {
        return optional.isPresent() ? DoubleStream.of(optional.getAsDouble()) : DoubleStream.empty();
    }
    
    @SafeVarargs
    public static <T> Stream<T> concat(final Stream<? extends T>... streams) {
        boolean isParallel = false;
        int characteristics = 336;
        long estimatedSize = 0L;
        final ImmutableList.Builder<Spliterator<? extends T>> splitrsBuilder = new ImmutableList.Builder<Spliterator<? extends T>>(streams.length);
        for (final Stream<? extends T> stream : streams) {
            isParallel |= stream.isParallel();
            final Spliterator<? extends T> splitr2 = stream.spliterator();
            splitrsBuilder.add(splitr2);
            characteristics &= splitr2.characteristics();
            estimatedSize = LongMath.saturatedAdd(estimatedSize, splitr2.estimateSize());
        }
        int length2;
        int j = 0;
        Stream<?> stream2;
        return (Stream<T>)StreamSupport.stream((Spliterator<Object>)CollectSpliterators.flatMap(splitrsBuilder.build().spliterator(), splitr -> splitr, characteristics, estimatedSize), isParallel).onClose(() -> {
            for (length2 = streams.length; j < length2; ++j) {
                stream2 = streams[j];
                stream2.close();
            }
        });
    }
    
    public static IntStream concat(final IntStream... streams) {
        return Stream.of(streams).flatMapToInt(stream -> stream);
    }
    
    public static LongStream concat(final LongStream... streams) {
        return Stream.of(streams).flatMapToLong(stream -> stream);
    }
    
    public static DoubleStream concat(final DoubleStream... streams) {
        return Stream.of(streams).flatMapToDouble(stream -> stream);
    }
    
    public static <A, B, R> Stream<R> zip(final Stream<A> streamA, final Stream<B> streamB, final BiFunction<? super A, ? super B, R> function) {
        Preconditions.checkNotNull(streamA);
        Preconditions.checkNotNull(streamB);
        Preconditions.checkNotNull(function);
        final boolean isParallel = streamA.isParallel() || streamB.isParallel();
        final Spliterator<A> splitrA = streamA.spliterator();
        final Spliterator<B> splitrB = streamB.spliterator();
        final int characteristics = splitrA.characteristics() & splitrB.characteristics() & 0x50;
        final Iterator<A> itrA = Spliterators.iterator((Spliterator<? extends A>)splitrA);
        final Iterator<B> itrB = Spliterators.iterator((Spliterator<? extends B>)splitrB);
        return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new Spliterators.AbstractSpliterator<R>(Math.min(splitrA.estimateSize(), splitrB.estimateSize()), characteristics) {
            @Override
            public boolean tryAdvance(final Consumer<? super R> action) {
                if (itrA.hasNext() && itrB.hasNext()) {
                    action.accept(function.apply(itrA.next(), itrB.next()));
                    return true;
                }
                return false;
            }
        }, isParallel).onClose(streamA::close).onClose(streamB::close);
    }
    
    public static <A, B> void forEachPair(final Stream<A> streamA, final Stream<B> streamB, final BiConsumer<? super A, ? super B> consumer) {
        Preconditions.checkNotNull(consumer);
        if (streamA.isParallel() || streamB.isParallel()) {
            zip(streamA, streamB, TemporaryPair::new).forEach(pair -> consumer.accept((Object)pair.a, (Object)pair.b));
        }
        else {
            final Iterator<A> iterA = streamA.iterator();
            final Iterator<B> iterB = streamB.iterator();
            while (iterA.hasNext() && iterB.hasNext()) {
                consumer.accept((Object)iterA.next(), (Object)iterB.next());
            }
        }
    }
    
    public static <T, R> Stream<R> mapWithIndex(final Stream<T> stream, final FunctionWithIndex<? super T, ? extends R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        final boolean isParallel = stream.isParallel();
        final Spliterator<T> fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final Iterator<T> fromIterator = Spliterators.iterator((Spliterator<? extends T>)fromSpliterator);
            return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50) {
                long index = 0L;
                
                @Override
                public boolean tryAdvance(final Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.next(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class 1Splitr extends MapWithIndexSpliterator<Spliterator<T>, R, 1Splitr> implements Consumer<T>
        {
            T holder;
            final /* synthetic */ FunctionWithIndex val$function;
            
            1Splitr(final Spliterator splitr, final Spliterator<T> index, final long p3) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     3: putfield        rp/com/google/common/collect/Streams$1Splitr.val$function:Lrp/com/google/common/collect/Streams$FunctionWithIndex;
                //     6: aload_0         /* this */
                //     7: aload_1         /* splitr */
                //     8: lload_2         /* index */
                //     9: invokespecial   rp/com/google/common/collect/Streams$MapWithIndexSpliterator.<init>:(Ljava/util/Spliterator;J)V
                //    12: return         
                //    Signature:
                //  (Ljava/util/Spliterator<TT;>;J)V
                // 
                // The error that occurred was:
                // 
                // java.lang.NullPointerException
                //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
                //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:670)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
            
            @Override
            public void accept(final T t) {
                this.holder = t;
            }
            
            @Override
            public boolean tryAdvance(final Consumer<? super R> action) {
                if (this.fromSpliterator.tryAdvance(this)) {
                    try {
                        action.accept(this.val$function.apply(this.holder, this.index++));
                        return true;
                    }
                    finally {
                        this.holder = null;
                    }
                }
                return false;
            }
            
            @Override
            1Splitr createSplit(final Spliterator<T> from, final long i) {
                return new 1Splitr(from, i, this.val$function);
            }
        }
        return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new 1Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }
    
    public static <R> Stream<R> mapWithIndex(final IntStream stream, final IntFunctionWithIndex<R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        final boolean isParallel = stream.isParallel();
        final Spliterator.OfInt fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfInt fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50) {
                long index = 0L;
                
                @Override
                public boolean tryAdvance(final Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.nextInt(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class 2Splitr extends MapWithIndexSpliterator<OfInt, R, 2Splitr> implements IntConsumer, Spliterator<R>
        {
            int holder;
            final /* synthetic */ IntFunctionWithIndex val$function;
            
            2Splitr(final OfInt splitr, final OfInt index, final long p3) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     3: putfield        rp/com/google/common/collect/Streams$2Splitr.val$function:Lrp/com/google/common/collect/Streams$IntFunctionWithIndex;
                //     6: aload_0         /* this */
                //     7: aload_1         /* splitr */
                //     8: lload_2         /* index */
                //     9: invokespecial   rp/com/google/common/collect/Streams$MapWithIndexSpliterator.<init>:(Ljava/util/Spliterator;J)V
                //    12: return         
                //    Signature:
                //  (Ljava/util/Spliterator$OfInt;J)V
                // 
                // The error that occurred was:
                // 
                // java.lang.NullPointerException
                //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
                //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:670)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
            
            @Override
            public void accept(final int t) {
                this.holder = t;
            }
            
            @Override
            public boolean tryAdvance(final Consumer<? super R> action) {
                if (((OfInt)this.fromSpliterator).tryAdvance((IntConsumer)this)) {
                    action.accept(this.val$function.apply(this.holder, this.index++));
                    return true;
                }
                return false;
            }
            
            @Override
            2Splitr createSplit(final OfInt from, final long i) {
                return new 2Splitr(from, i, this.val$function);
            }
        }
        return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new 2Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }
    
    public static <R> Stream<R> mapWithIndex(final LongStream stream, final LongFunctionWithIndex<R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        final boolean isParallel = stream.isParallel();
        final Spliterator.OfLong fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfLong fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50) {
                long index = 0L;
                
                @Override
                public boolean tryAdvance(final Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.nextLong(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class 3Splitr extends MapWithIndexSpliterator<OfLong, R, 3Splitr> implements LongConsumer, Spliterator<R>
        {
            long holder;
            final /* synthetic */ LongFunctionWithIndex val$function;
            
            3Splitr(final OfLong splitr, final OfLong index, final long p3) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     3: putfield        rp/com/google/common/collect/Streams$3Splitr.val$function:Lrp/com/google/common/collect/Streams$LongFunctionWithIndex;
                //     6: aload_0         /* this */
                //     7: aload_1         /* splitr */
                //     8: lload_2         /* index */
                //     9: invokespecial   rp/com/google/common/collect/Streams$MapWithIndexSpliterator.<init>:(Ljava/util/Spliterator;J)V
                //    12: return         
                //    Signature:
                //  (Ljava/util/Spliterator$OfLong;J)V
                // 
                // The error that occurred was:
                // 
                // java.lang.NullPointerException
                //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
                //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:670)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
            
            @Override
            public void accept(final long t) {
                this.holder = t;
            }
            
            @Override
            public boolean tryAdvance(final Consumer<? super R> action) {
                if (((OfLong)this.fromSpliterator).tryAdvance((LongConsumer)this)) {
                    action.accept(this.val$function.apply(this.holder, this.index++));
                    return true;
                }
                return false;
            }
            
            @Override
            3Splitr createSplit(final OfLong from, final long i) {
                return new 3Splitr(from, i, this.val$function);
            }
        }
        return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new 3Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }
    
    public static <R> Stream<R> mapWithIndex(final DoubleStream stream, final DoubleFunctionWithIndex<R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        final boolean isParallel = stream.isParallel();
        final Spliterator.OfDouble fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfDouble fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50) {
                long index = 0L;
                
                @Override
                public boolean tryAdvance(final Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.nextDouble(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class 4Splitr extends MapWithIndexSpliterator<OfDouble, R, 4Splitr> implements DoubleConsumer, Spliterator<R>
        {
            double holder;
            final /* synthetic */ DoubleFunctionWithIndex val$function;
            
            4Splitr(final OfDouble splitr, final OfDouble index, final long p3) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     3: putfield        rp/com/google/common/collect/Streams$4Splitr.val$function:Lrp/com/google/common/collect/Streams$DoubleFunctionWithIndex;
                //     6: aload_0         /* this */
                //     7: aload_1         /* splitr */
                //     8: lload_2         /* index */
                //     9: invokespecial   rp/com/google/common/collect/Streams$MapWithIndexSpliterator.<init>:(Ljava/util/Spliterator;J)V
                //    12: return         
                //    Signature:
                //  (Ljava/util/Spliterator$OfDouble;J)V
                // 
                // The error that occurred was:
                // 
                // java.lang.NullPointerException
                //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
                //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:713)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:549)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:670)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
            
            @Override
            public void accept(final double t) {
                this.holder = t;
            }
            
            @Override
            public boolean tryAdvance(final Consumer<? super R> action) {
                if (((OfDouble)this.fromSpliterator).tryAdvance((DoubleConsumer)this)) {
                    action.accept(this.val$function.apply(this.holder, this.index++));
                    return true;
                }
                return false;
            }
            
            @Override
            4Splitr createSplit(final OfDouble from, final long i) {
                return new 4Splitr(from, i, this.val$function);
            }
        }
        return (Stream<R>)StreamSupport.stream((Spliterator<Object>)new 4Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }
    
    public static <T> java.util.Optional<T> findLast(final Stream<T> stream) {
        class 1OptionalState
        {
            boolean set;
            T value;
            
            1OptionalState() {
                this.set = false;
                this.value = null;
            }
            
            void set(final T value) {
                this.set = true;
                this.value = value;
            }
            
            T get() {
                Preconditions.checkState(this.set);
                return this.value;
            }
        }
        final 1OptionalState state = new 1OptionalState();
        final Deque<Spliterator<T>> splits = new ArrayDeque<Spliterator<T>>();
        splits.addLast(stream.spliterator());
        while (!splits.isEmpty()) {
            Spliterator<T> spliterator = splits.removeLast();
            if (spliterator.getExactSizeIfKnown() == 0L) {
                continue;
            }
            if (spliterator.hasCharacteristics(16384)) {
                while (true) {
                    final Spliterator<T> prefix = spliterator.trySplit();
                    if (prefix == null) {
                        break;
                    }
                    if (prefix.getExactSizeIfKnown() == 0L) {
                        break;
                    }
                    if (spliterator.getExactSizeIfKnown() == 0L) {
                        spliterator = prefix;
                        break;
                    }
                }
                spliterator.forEachRemaining(state::set);
                return java.util.Optional.of(state.get());
            }
            final Spliterator<T> prefix = spliterator.trySplit();
            if (prefix == null || prefix.getExactSizeIfKnown() == 0L) {
                spliterator.forEachRemaining(state::set);
                if (state.set) {
                    return java.util.Optional.of(state.get());
                }
                continue;
            }
            else {
                splits.addLast(prefix);
                splits.addLast(spliterator);
            }
        }
        return java.util.Optional.empty();
    }
    
    public static OptionalInt findLast(final IntStream stream) {
        final java.util.Optional<Integer> boxedLast = findLast(stream.boxed());
        return boxedLast.isPresent() ? OptionalInt.of(boxedLast.get()) : OptionalInt.empty();
    }
    
    public static OptionalLong findLast(final LongStream stream) {
        final java.util.Optional<Long> boxedLast = findLast(stream.boxed());
        return boxedLast.isPresent() ? OptionalLong.of(boxedLast.get()) : OptionalLong.empty();
    }
    
    public static OptionalDouble findLast(final DoubleStream stream) {
        final java.util.Optional<Double> boxedLast = findLast(stream.boxed());
        return boxedLast.isPresent() ? OptionalDouble.of(boxedLast.get()) : OptionalDouble.empty();
    }
    
    private Streams() {
    }
    
    private static class TemporaryPair<A, B>
    {
        final A a;
        final B b;
        
        TemporaryPair(final A a, final B b) {
            this.a = a;
            this.b = b;
        }
    }
    
    private abstract static class MapWithIndexSpliterator<F extends Spliterator<?>, R, S extends MapWithIndexSpliterator<F, R, S>> implements Spliterator<R>
    {
        final F fromSpliterator;
        long index;
        
        MapWithIndexSpliterator(final F fromSpliterator, final long index) {
            this.fromSpliterator = fromSpliterator;
            this.index = index;
        }
        
        abstract S createSplit(final F p0, final long p1);
        
        @Override
        public S trySplit() {
            final F split = (F)this.fromSpliterator.trySplit();
            if (split == null) {
                return null;
            }
            final S result = this.createSplit(split, this.index);
            this.index += split.getExactSizeIfKnown();
            return result;
        }
        
        @Override
        public long estimateSize() {
            return this.fromSpliterator.estimateSize();
        }
        
        @Override
        public int characteristics() {
            return this.fromSpliterator.characteristics() & 0x4050;
        }
    }
    
    @Beta
    public interface DoubleFunctionWithIndex<R>
    {
        R apply(final double p0, final long p1);
    }
    
    @Beta
    public interface LongFunctionWithIndex<R>
    {
        R apply(final long p0, final long p1);
    }
    
    @Beta
    public interface IntFunctionWithIndex<R>
    {
        R apply(final int p0, final long p1);
    }
    
    @Beta
    public interface FunctionWithIndex<T, R>
    {
        R apply(final T p0, final long p1);
    }
}
