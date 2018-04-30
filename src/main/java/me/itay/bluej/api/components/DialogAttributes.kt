package me.itay.bluej.api.components

/**
 * An easier way to handle dialog crashes and to decorate them
 *
 * A dialog attribute is an attribute to [BlueJDialog] that have a built in throwable
 *  that will be invoked when the attribute's value matches the boolean expression
 *  passed into the super constructor of every sealed subclass
 */
sealed class DialogAttr(
        open val name: String,
        open var bounds: Boolean = true
)

/////////////////////////////////////////////////////////////////////////////////////////

/**
 * A boundless string. It can be an empty or blank string. No sweat.
 */
open class StringAttr(
        override val name: String,
        open val attrVal: String,
        override var bounds: Boolean = true
) : DialogAttr(
        name,
        bounds
)

/**
 * A string attribute that cannot be blank (not empty ("") and not containing only whitespace).
 */
class NonBlankStringAttr(
        override val name: String,
        override val attrVal: String
) : StringAttr(
        name,
        attrVal,
        attrVal.isNotBlank()
)

/////////////////////////////////////////////////////////////////////////////////////////

/**
 * A signed integer attribute. Use this when you cannot have negatives.
 */
open class SignedIntegerAttr(
        override val name: String,
        open val attrVal: Int,
        override var bounds: Boolean
) : DialogAttr(
        name,
        bounds
)
/**
 * An unsigned integer attribute. Use this when the value is not bound to any kind of condition.
 */
class UnsignedIntegerAttr(
        override val name: String,
        override val attrVal: Int
) : SignedIntegerAttr(
        name,
        attrVal,
        attrVal > 0
)

/////////////////////////////////////////////////////////////////////////////////////////

/**
 * Use this for bigger and more complex calculations, otherwise, please refrain from using this attribute!
 *
 * This double attribute has no bounds to negativity
 */
open class UnsignedDoubleAttr(
        override val name: String,
        open val attrVal: Double,
        override var bounds: Boolean
) : DialogAttr(
        name,
        bounds
)

/**
 * Use this if you need positive only doubles
 */
class SignedDoubleAttr(
        override val name: String,
        override val attrVal: Double
) : UnsignedDoubleAttr(
        name,
        attrVal,
        attrVal > 0
)

/////////////////////////////////////////////////////////////////////////////////////////

/**
 * Use this whenever possible, otherwise, [UnsignedDoubleAttr] is provided
 *
 * An unsigned float attribute. Use this when you do not need
 */
open class UnsignedFloatAttr(
        override val name: String,
        open val attrVal: Float,
        override var bounds: Boolean
) : DialogAttr(
        name,
        bounds
)
/**
 * A signed float attribute. Use this when you can have negative float values
 */
class SignedFloatAttr(
        override val name: String,
        override val attrVal: Float
) : UnsignedFloatAttr(
        name,
        attrVal,
        attrVal > 0F
)