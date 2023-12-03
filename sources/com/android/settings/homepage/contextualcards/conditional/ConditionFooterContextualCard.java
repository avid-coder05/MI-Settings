package com.android.settings.homepage.contextualcards.conditional;

import com.android.settings.homepage.contextualcards.ContextualCard;

/* loaded from: classes.dex */
public class ConditionFooterContextualCard extends ContextualCard {

    /* loaded from: classes.dex */
    public static class Builder extends ContextualCard.Builder {
        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public ConditionFooterContextualCard build() {
            return new ConditionFooterContextualCard(this);
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public Builder setCardType(int i) {
            throw new IllegalArgumentException("Cannot change card type for " + Builder.class.getName());
        }
    }

    private ConditionFooterContextualCard(Builder builder) {
        super(builder);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public int getCardType() {
        return 5;
    }
}
