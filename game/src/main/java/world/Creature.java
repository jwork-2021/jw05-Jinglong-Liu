/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package world;

import java.awt.Color;

/**
 *
 * @author Aeranythe Echosong
 */
public class Creature extends Thing implements Runnable{
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    private int visionRadius;

    public int visionRadius() {
        return this.visionRadius;
    }
    public boolean canSee(int wx, int wy) {
        //return ai.canSee(wx, wy);
        return true;
    }

    public Creature(World world, char glyph, Color color, int maxHP, int attack, int defense, int visionRadius) {
        super(world,glyph,color,maxHP,attack,defense,visionRadius);
    }
    public void fire(Bullet bullet){
        //threadPool.execute(bullet);
    }
}
