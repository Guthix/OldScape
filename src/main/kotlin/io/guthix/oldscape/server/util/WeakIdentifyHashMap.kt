/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.util

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class WeakIdentityHashMap<K : Any, V> : MutableMap<K, V> {
    private val queue = ReferenceQueue<K>()

    private val backingstore: MutableMap<IdentityWeakReference, V> = hashMapOf()

    override operator fun get(key: K): V? {
        reap()
        return backingstore[IdentityWeakReference(key)]
    }

    override fun put(key: K, value: V): V? {
        reap()
        return backingstore.put(IdentityWeakReference(key), value)
    }

    override fun remove(key: K): V? {
        reap()
        return backingstore.remove(IdentityWeakReference(key))
    }

    override fun remove(key: K, value: V): Boolean {
        reap()
        return backingstore.remove(IdentityWeakReference(key), value)
    }

    override fun putAll(from: Map<out K, V>) {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        backingstore.clear()
        reap()
    }

    override fun containsKey(key: K): Boolean {
        reap()
        return backingstore.containsKey(IdentityWeakReference(key))
    }

    override fun containsValue(value: V): Boolean {
        reap()
        return backingstore.containsValue(value)
    }

    override val size get(): Int {
        reap()
        return backingstore.size
    }

    override val keys: MutableSet<K> get() {
        reap()
        return backingstore.keys.mapNotNull { it.get() }.toMutableSet()
    }

    override val values get(): MutableCollection<V> {
        reap()
        return backingstore.values
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get(){
        reap()
        val ret: MutableSet<MutableMap.MutableEntry<K, V>> = hashSetOf()
        for ((key1, value) in backingstore) {
            val key = key1.get()
            if (key != null) {
                val entry: MutableMap.MutableEntry<K, V> = object : MutableMap.MutableEntry<K, V> {
                    override val key: K get() = key

                    override val value: V get() = value

                    override fun setValue(newValue: V): V  = throw UnsupportedOperationException()
                }
                ret.add(entry)
            }
        }
        return ret
    }

    override fun equals(other: Any?): Boolean {
        if (other !is WeakIdentityHashMap<*, *>) {
            return false
        }
        return backingstore == other.backingstore
    }

    override fun hashCode(): Int {
        reap()
        return backingstore.hashCode()
    }

    override fun isEmpty(): Boolean {
        reap()
        return backingstore.isEmpty()
    }

    private fun reap() {
        var ref: Reference<out K>? = queue.poll()
        while (ref != null) {
            backingstore.remove(ref)
            ref = queue.poll()
        }
    }

    internal inner class IdentityWeakReference(obj: K) : WeakReference<K>(obj, queue) {
        private var hash: Int = System.identityHashCode(obj)

        override fun hashCode(): Int {
            return hash
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other is WeakReference<*>) {
                if (this.get() === other.get()) {
                    return true
                }
            }
            return false
        }
    }
}
