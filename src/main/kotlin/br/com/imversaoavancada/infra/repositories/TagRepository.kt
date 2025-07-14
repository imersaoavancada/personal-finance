package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Tag
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TagRepository : PanacheRepositoryBase<Tag, Long>
