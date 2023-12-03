package com.google.android.play.core.tasks;

import java.util.concurrent.Executor;

/* loaded from: classes2.dex */
public abstract class Task<Result> {
    public abstract Task<Result> addOnCompleteListener(OnCompleteListener<Result> onCompleteListener);

    public abstract Task<Result> addOnCompleteListener(Executor executor, OnCompleteListener<Result> onCompleteListener);

    public abstract Task<Result> addOnFailureListener(OnFailureListener onFailureListener);

    public abstract Task<Result> addOnFailureListener(Executor executor, OnFailureListener onFailureListener);

    public abstract Task<Result> addOnSuccessListener(OnSuccessListener<? super Result> onSuccessListener);

    public abstract Task<Result> addOnSuccessListener(Executor executor, OnSuccessListener<? super Result> onSuccessListener);

    public abstract Exception getException();

    public abstract Result getResult();

    public abstract <X extends Throwable> Result getResult(Class<X> cls) throws Throwable;

    public abstract boolean isComplete();

    public abstract boolean isSuccessful();
}
