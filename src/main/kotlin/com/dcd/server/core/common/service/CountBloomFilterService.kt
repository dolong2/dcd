package com.dcd.server.core.common.service

interface CountBloomFilterService {
    fun add(filterName: String, item: String): Boolean
    fun remove(filterName: String, item: String): Boolean
    fun exists(filterName: String, item: String): Boolean
}