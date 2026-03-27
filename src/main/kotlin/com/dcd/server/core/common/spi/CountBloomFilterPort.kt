package com.dcd.server.core.common.spi

interface CountBloomFilterPort {
    fun add(filterName: String, item: String): Boolean
    fun remove(filterName: String, item: String): Boolean
    fun exists(filterName: String, item: String): Boolean
}
