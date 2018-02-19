/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
*/

package com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay.edge;

/**
 * Enum representing an edge in the crop window.
 */
public enum Edge {

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;


    // Member Variables ////////////////////////////////////////////////////////

    private float mCoordinate;

    // Public Methods //////////////////////////////////////////////////////////

    /**
     * Sets the coordinate of the Edge. The coordinate will represent the
     * x-coordinate for LEFT and RIGHT Edges and the y-coordinate for TOP and
     * BOTTOM edges.
     * 
     * @param coordinate the position of the edge
     */
    public void setCoordinate(float coordinate) {
        mCoordinate = coordinate;
    }

    /**
     * Gets the coordinate of the Edge
     * 
     * @return the Edge coordinate (x-coordinate for LEFT and RIGHT Edges and
     *         the y-coordinate for TOP and BOTTOM edges)
     */
    public float getCoordinate() {
        return mCoordinate;
    }


    /**
     * Gets the current width of the crop window.
     */
    public static float getWidth() {
        return Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate();
    }

    /**
     * Gets the current height of the crop window.
     */
    public static float getHeight() {
        return Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate();
    }

}
