package me.itay.bluej.resourcelocation

import me.itay.bluej.resourcelocation.builtin.file.BlueJFileSystemResolver
import java.util.HashMap

private val resolvers = HashMap<String, BlueJResolver>()

fun init(){
    registerResolver("file", BlueJFileSystemResolver())
}

fun registerResolver(domain: String, resolver: BlueJResolver) {
    if (resolvers.containsKey(domain)) {
        throw IllegalArgumentException("resolver for that domain already exists")
    }
    resolvers[domain] = resolver
}

fun resolve(path: String): BlueJResolvedResource? {
    return resolve(BlueJResourceLocation(path))
}

fun resolve(location: BlueJResourceLocation): BlueJResolvedResource? {
    if (resolvers.containsKey(location.domain)) {
        if (location.isBase) {
            return resolvers[location.domain]!!.resolve(location, null)
        }
        val base = resolve(location.parent)
        return resolvers[location.domain]!!.resolve(location, base)
    }
    return null
}