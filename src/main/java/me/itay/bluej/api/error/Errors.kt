package me.itay.bluej.api.error

import me.itay.bluej.api.components.DialogAttr

/**
 * An error thrown when a dialog attribute runs into a problem.
 * TODO: Create more errors
 */
open class DialogAttributeError(
        attr: DialogAttr,
        error: String
) : Error(
        "A dialog attribute has caught an error: ${attr.name}",
        Throwable(error)
)

class DialogAttributeValueOutOfBoundsError(
        attr: DialogAttr
) : DialogAttributeError(
        attr,
        "Attribute value is out of bounds: ${attr::bounds}"
)

/**
 * The root of all BlueJ related exceptions
 */
sealed class BlueJException(
        override val message: String
) : Exception(
        "BlueJ encountered a problem",
        Throwable(message),
        false,
        true
)

/**
 * An exception that is both displayed and logged
 */
sealed class LoggedException(
        open override val message: String
) : BlueJException(message)

/**
 * An exception that is only displayed
 * TODO: Allow the option of logging for debugging purposes
 */
sealed class DisplayedException(
        open override val message: String
) : BlueJException(message)

/**
 * An exception to be thrown when a project isn't loaded. This should only be thrown when there is a bug in the code.
 * Never user error!!
 */
class NoProjectLoadedException : LoggedException(
        "No project is loaded! This should not happen! Report to the author!"
)

/**
 * An exception that will abort the runtime startup process if there is no startup file specified
 */
class NoStartupFileSpecifiedException : DisplayedException("No startup file! Aborting runtime!")

/**
 * An exception that is thrown when there isn't a source file selected for a particular action
 */
class NoSourceFileSelectedException : DisplayedException("No source file selected!")

/**
 * If the component currently being interacted with is not toggled/enabled, this will be thrown
 */
class BlueJComponentToggleException(componentName: String) : DisplayedException("The $componentName component is not toggled!")