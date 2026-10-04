package client

@RequiresOptIn(message = "This API is still unfinished and is not meant to be used. Anything can change.")
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR
)
annotation class GDClientApi()
