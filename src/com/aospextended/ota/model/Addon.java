/*
 * Copyright (C) 2019 AospExtended ROM
 *
 * * Licensed under the GNU GPLv2 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl-2.0.txt
 */

package com.aospextended.ota.model;


public class Addon {
     public String title;
     public String summary;
     public String url;

     public String getTitle() {
        return this.title;
     }

     public String getSummary() {
        return this.summary;
     }

     public String getUrl() {
        return this.url;
     }
}
