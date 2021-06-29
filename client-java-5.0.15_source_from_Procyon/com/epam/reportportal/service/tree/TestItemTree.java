// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.tree;

import javax.annotation.Nonnull;
import com.epam.reportportal.listeners.ItemType;
import com.epam.reportportal.listeners.ItemStatus;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import io.reactivex.Maybe;

public class TestItemTree
{
    private Maybe<String> launchId;
    private final Map<ItemTreeKey, TestItemLeaf> testItems;
    
    public TestItemTree() {
        this.testItems = new ConcurrentHashMap<ItemTreeKey, TestItemLeaf>();
    }
    
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> itemId) {
        return new TestItemLeaf((Maybe)itemId);
    }
    
    @Deprecated
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> itemId, final int expectedChildrenCount) {
        return createTestItemLeaf(itemId);
    }
    
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> parentId, final Maybe<String> itemId) {
        return new TestItemLeaf((Maybe)parentId, (Maybe)itemId);
    }
    
    @Deprecated
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> parentId, final Maybe<String> itemId, final int expectedChildrenCount) {
        return createTestItemLeaf(parentId, itemId);
    }
    
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> itemId, final Map<ItemTreeKey, TestItemLeaf> childItems) {
        return new TestItemLeaf((Maybe)itemId, (Map)Collections.emptyMap(), (Map)childItems);
    }
    
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> itemId, final Map<ItemTreeKey, TestItemLeaf> childItems, final Map<String, Object> attributes) {
        return new TestItemLeaf((Maybe)itemId, (Map)attributes, (Map)childItems);
    }
    
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> parentId, final Maybe<String> itemId, final Map<ItemTreeKey, TestItemLeaf> childItems) {
        return new TestItemLeaf((Maybe)parentId, (Maybe)itemId, (Map)Collections.emptyMap(), (Map)childItems);
    }
    
    public static TestItemLeaf createTestItemLeaf(final Maybe<String> parentId, final Maybe<String> itemId, final Map<ItemTreeKey, TestItemLeaf> childItems, final Map<String, Object> attributes) {
        return new TestItemLeaf((Maybe)parentId, (Maybe)itemId, (Map)attributes, (Map)childItems);
    }
    
    public Maybe<String> getLaunchId() {
        return this.launchId;
    }
    
    public void setLaunchId(final Maybe<String> launchId) {
        this.launchId = launchId;
    }
    
    public Map<ItemTreeKey, TestItemLeaf> getTestItems() {
        return this.testItems;
    }
    
    public static final class ItemTreeKey
    {
        private final String name;
        private final int hash;
        
        private ItemTreeKey(final String name, final int hash) {
            this.name = name;
            this.hash = hash;
        }
        
        private ItemTreeKey(final String name) {
            this(name, (name != null) ? name.hashCode() : 0);
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getHash() {
            return this.hash;
        }
        
        public static ItemTreeKey of(final String name) {
            return new ItemTreeKey(name);
        }
        
        public static ItemTreeKey of(final String name, final int hash) {
            return new ItemTreeKey(name, hash);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ItemTreeKey that = (ItemTreeKey)o;
            return this.hash == that.hash && Objects.equals(this.name, that.name);
        }
        
        @Override
        public int hashCode() {
            int result = (this.name != null) ? this.name.hashCode() : 0;
            result = 31 * result + this.hash;
            return result;
        }
    }
    
    public static class TestItemLeaf
    {
        @Nullable
        private Maybe<String> parentId;
        @Nullable
        private Maybe<OperationCompletionRS> finishResponse;
        private final Maybe<String> itemId;
        private final Map<ItemTreeKey, TestItemLeaf> childItems;
        private final Map<String, Object> attributes;
        private ItemStatus status;
        private ItemType type;
        
        private TestItemLeaf(final Maybe<String> itemId) {
            this.childItems = new ConcurrentHashMap<ItemTreeKey, TestItemLeaf>();
            this.attributes = new ConcurrentHashMap<String, Object>();
            this.itemId = itemId;
        }
        
        private TestItemLeaf(final Maybe<String> itemId, final Map<String, Object> attributes) {
            this(itemId);
            this.attributes.putAll(attributes);
        }
        
        private TestItemLeaf(final Maybe<String> itemId, final Map<String, Object> attributes, final Map<ItemTreeKey, TestItemLeaf> childItems) {
            this(itemId, attributes);
            this.childItems.putAll(childItems);
        }
        
        private TestItemLeaf(@Nullable final Maybe<String> parentId, final Maybe<String> itemId) {
            this(itemId);
            this.parentId = parentId;
        }
        
        private TestItemLeaf(@Nullable final Maybe<String> parentId, final Maybe<String> itemId, final Map<String, Object> attributes) {
            this(itemId, attributes);
            this.parentId = parentId;
        }
        
        private TestItemLeaf(@Nullable final Maybe<String> parentId, final Maybe<String> itemId, final Map<String, Object> attributes, final Map<ItemTreeKey, TestItemLeaf> childItems) {
            this(itemId, parentId, attributes);
            this.childItems.putAll(childItems);
        }
        
        @Nullable
        public Maybe<String> getParentId() {
            return this.parentId;
        }
        
        public void setParentId(@Nullable final Maybe<String> parentId) {
            this.parentId = parentId;
        }
        
        @Nullable
        public Maybe<OperationCompletionRS> getFinishResponse() {
            return this.finishResponse;
        }
        
        public void setFinishResponse(@Nullable final Maybe<OperationCompletionRS> finishResponse) {
            this.finishResponse = finishResponse;
        }
        
        public Maybe<String> getItemId() {
            return this.itemId;
        }
        
        @Nonnull
        public Map<ItemTreeKey, TestItemLeaf> getChildItems() {
            return this.childItems;
        }
        
        public ItemStatus getStatus() {
            return this.status;
        }
        
        public void setStatus(final ItemStatus status) {
            this.status = status;
        }
        
        public ItemType getType() {
            return this.type;
        }
        
        public void setType(final ItemType type) {
            this.type = type;
        }
        
        @Nullable
        public <T> T getAttribute(final String key) {
            return (T)this.attributes.get(key);
        }
        
        @Nullable
        public Object setAttribute(final String key, final Object value) {
            return this.attributes.put(key, value);
        }
        
        @Nullable
        public Object clearAttribute(final String key) {
            return this.attributes.remove(key);
        }
        
        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap((Map<? extends String, ?>)this.attributes);
        }
    }
}
