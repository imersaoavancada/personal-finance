package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Tag
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Douglas O. Luciano
 */
@ApplicationScoped
class TagRepository : AbstractRepository<Tag>() {
    override val searchQuery = "LOWER(name) LIKE ?1"
}
