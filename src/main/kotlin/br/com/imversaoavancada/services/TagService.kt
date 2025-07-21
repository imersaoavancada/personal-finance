package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Tag
import br.com.imversaoavancada.infra.repositories.TagRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException

/**
 * @author Douglas O. Luciano
 */
@ApplicationScoped
class TagService(
    val repository: TagRepository,
) {
    fun count(term: String?): Long = repository.count(term)

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<Tag> = repository.list(page, size, term)

    fun getById(id: Long): Tag =
        repository.findById(id)
            ?: throw NotFoundException()

    @Transactional
    fun create(tag: Tag): Tag {
        repository.persist(tag)
        return tag
    }

    @Transactional
    fun update(
        id: Long,
        tag: Tag,
    ): Tag {
        val persisted = getById(id)

        persisted.name = tag.name
        persisted.color = tag.color

        repository.persist(persisted)

        return persisted
    }

    @Transactional
    fun delete(id: Long) = repository.delete(getById(id))
}
