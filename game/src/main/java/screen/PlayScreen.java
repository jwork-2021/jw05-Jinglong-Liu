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
package screen;

import world.*;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen implements Screen {

    private World world;
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private List<String> oldMessages;
    private CreatureFactory factory;
    //private ExecutorService threadPool = Executors.newCachedThreadPool();
    public PlayScreen() {
        this.screenWidth = 45;
        this.screenHeight = 45;
        Enemy.enemyNum = new AtomicInteger(8);
        this.messages = new CopyOnWriteArrayList<>();
        this.oldMessages = new ArrayList<String>();
        createWorld();
        
        factory = new CreatureFactory(this.world);
        createCreatures(factory);
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(200);
                        for(Thing bullet:world.bullets()){
                            bullet.step();
                        }
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        this.player = creatureFactory.newPlayer(this.messages);

        for (int i = 0; i < Enemy.enemyNum.get();i++) {
            //creatureFactory.newFungus();
            new Thread(creatureFactory.newEnemy(this.messages)).start();
        }
    }
    private void createWorld() {
        world = new WorldBuilder(45,45).makeCaves().makeCamp().build(this.messages);
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;

                if (player.canSee(wx, wy)) {
                    terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
                } else {
                    //terminal.write(world.glyph(wx, wy), x, y, Color.DARK_GRAY);
                }
            }
        }
        // Show creatures
        for (Thing creature : world.getThings()) {
            
            if (creature.x() >= left && creature.x() < left + screenWidth && creature.y() >= top
                    && creature.y() < top + screenHeight) {
                if (player.canSee(creature.x(), creature.y())) {
                    terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
                }
            }
        }
        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = this.screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++) {
            terminal.write(messages.get(i), 1, top + i + 3);
        }
        this.oldMessages.addAll(messages);

        while(messages.size() > 1){
            messages.remove(0);
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // Terrain and creatures
        displayTiles(terminal, getScrollX(), getScrollY());
        // Player
        terminal.write(player.glyph(), player.x() - getScrollX(), player.y() - getScrollY(), player.color());
        // Stats
        String stats = String.format("%3d/%3d hp", player.hp(), player.maxHP());
        //System.out.println( player.hp());
        terminal.write(stats, 1, screenHeight + 1);
        // Messages
        displayMessages(terminal, this.messages);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        if(world.isLose()){
            return new LoseScreen();
        }
        else if(world.isWin()){
            return new WinScreen();
        }
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                player.turn(Direction.LEFT);
                player.moveBy(-1, 0,false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                player.turn(Direction.RIGHT);
                player.moveBy(1, 0,false);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                player.turn(Direction.UP);
                player.moveBy(0, -1,false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                player.turn(Direction.DOWN);
                player.moveBy(0, 1,false);
                break;
            case KeyEvent.VK_J:
            case KeyEvent.VK_SPACE:
                //player.fire(factory.newBullet(player));
                factory.newBullet(player);
                break;
        }
        return this;
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
    }
}
