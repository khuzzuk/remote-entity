package pl.javahello.entity.remote.test

import org.springframework.stereotype.Component
import spock.lang.Specification

import java.lang.reflect.Field
import java.util.stream.Collectors

class MainEntitySpec extends Specification {
    def 'check generated classes'() {
        given:
        Class adapterToEntityClass = Class.forName('pl.javahello.entity.remote.test.MainEntityAdapter')
        Class adapterToEntityImplClass = Class.forName('pl.javahello.entity.remote.test.MainEntityAdapterImpl')
        Class dtoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTO')
        Class adapterToDtoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTOAdapter')
        Class adapterToDtoImplClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTOAdapterImpl')
        Class repoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityRepo')
        Class serviceClass = Class.forName('pl.javahello.entity.remote.test.MainEntityService')

        expect:
        adapterToEntityClass
        adapterToEntityImplClass
        dtoClass
        adapterToDtoClass
        adapterToEntityImplClass
        repoClass
        serviceClass
    }

    def 'check dto class'() {
        given:
        Class dtoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTO')
        Field[] fields = dtoClass.getDeclaredFields()
        Set<String> fieldNames = Arrays.stream(fields).map({ it.name }).collect(Collectors.toSet())

        expect:
        fields.size() == 19
        fieldNames == [
                'byteField',
                'shortField',
                'intField',
                'longField',
                'floatField',
                'doubleField',
                'stringField',
                'internalField',
                'internalDataTransferObjectField',
                'nestedRemoteEntityField',
                'typeContainsDefaultNameField',

                'byteInheritedField',
                'shortInheritedField',
                'intInheritedField',
                'longInheritedField',
                'floatInheritedField',
                'doubleInheritedField',
                'stringInheritedField',
                'internalInheritedField',
        ] as Set
    }

    def 'check adapter to dto'() {
        given:
        Class adapter = Class.forName('pl.javahello.entity.remote.test.MainEntityDTOAdapterImpl')

        expect:
        adapter.getAnnotation(Component.class)
        adapter.getDeclaredField('internalTypeDTOAdapter')
        adapter.getDeclaredField('internalDataTransferObjectDTOAdapter')
        adapter.getDeclaredField('nestedRemoteEntityDTOAdapter')
        adapter.getDeclaredField('typeContainsDefaultName_intDTOAdapter')
    }
}
