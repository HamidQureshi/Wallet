package com.hamid.domain.model.repository

/**
 * Data mapper used to map database entities to model classes and vice versa.
 */
interface TransactionModelMapper<E, M> {
    fun fromEntity(from: E): M
//    fun toEntity(from: M): E
}