package pl.javahello.entity.remote.test

import spock.lang.Specification

import java.lang.reflect.Field
import java.util.stream.Collectors

class MainEntitySpec extends Specification {
    def 'check generated classes'() {
        given:
        Class adapterToEntityClass = Class.forName('pl.javahello.entity.remote.test.MainEntityAdapter')
        Class dtoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTO')
        Class adapterToDtoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTOAdapter')
        Class repoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityRepo')
        Class serviceClass = Class.forName('pl.javahello.entity.remote.test.MainEntityService')

        expect:
        adapterToEntityClass
        dtoClass
        adapterToDtoClass
        repoClass
        serviceClass
    }

    def 'check dto class'() {
        given:
        Class dtoClass = Class.forName('pl.javahello.entity.remote.test.MainEntityDTO')
        Field[] fields = dtoClass.getDeclaredFields()
        Set<String> fieldNames = Arrays.stream(fields).map({ it.name }).collect(Collectors.toSet())

        expect:
        fields.size() == 15
        fieldNames == [
                'byteField',//'shortField',
                'intField',
                'longField',
                'floatField',
                'doubleField',
                'stringField',
                'internalField',
                'internalDataTransferObjectField',

                'byteInheritedField',//'shortInheritedField',
                'intInheritedField',
                'longInheritedField',
                'floatInheritedField',
                'doubleInheritedField',
                'stringInheritedField',
                'internalInheritedField',
        ] as Set
    }
}
