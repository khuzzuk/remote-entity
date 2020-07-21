package pl.javahello.entity.remote

import spock.lang.Specification

class MainEntityTest extends Specification {
    def 'check if classes are generated'() {
        given:
        Class dtoClass = Class.forName('pl.javahello.entity.remote.MainEntityDTO')
        Class adapterToEntityClass = Class.forName('pl.javahello.entity.remote.MainEntityAdapter')
        Class adapterToEntityImplClass = Class.forName('pl.javahello.entity.remote.MainEntityAdapterImpl')
        Class adapterToDTOClass = Class.forName('pl.javahello.entity.remote.MainEntityDTOAdapter')
        Class adapterToDTOImplClass = Class.forName('pl.javahello.entity.remote.MainEntityDTOAdapterImpl')
        Class repoClass = Class.forName('pl.javahello.entity.remote.MainEntityRepo')
        Class serviceClass = Class.forName('pl.javahello.entity.remote.MainEntityService')

        expect:
        dtoClass
        adapterToEntityClass
        adapterToEntityImplClass
        adapterToDTOClass
        adapterToDTOImplClass
        repoClass
        serviceClass
    }
}
