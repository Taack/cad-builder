package org.taack.cad.dsl.builder

/**
 * From Profile, or Trimmed geometries to Topological Data Structure
 */
interface IEdge {
    IEdge[] fromProfile(IProfile profile)
}
