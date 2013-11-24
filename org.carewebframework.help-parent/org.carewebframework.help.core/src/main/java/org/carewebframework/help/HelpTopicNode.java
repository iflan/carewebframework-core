/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.help;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a single node in the help topic tree.
 */
public class HelpTopicNode {
    
    private final HelpTopic topic;
    
    private final LinkedList<HelpTopicNode> children = new LinkedList<HelpTopicNode>();
    
    private HelpTopicNode parent;
    
    /**
     * Create a topic node with its associated help topic.
     * 
     * @param topic A help topic.
     */
    public HelpTopicNode(HelpTopic topic) {
        this.topic = topic;
    }
    
    /**
     * Returns the child nodes of this node.
     * 
     * @return List of child nodes (never null).
     */
    public List<HelpTopicNode> getChildren() {
        return children;
    }
    
    /**
     * Returns the position of this node among its siblings.
     * 
     * @return Node position, or -1 if the node has no parent.
     */
    public int getIndex() {
        if (parent != null) {
            for (int i = 0; i < parent.children.size(); i++) {
                if (parent.children.get(i) == this) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Returns the help topic associated with this node.
     * 
     * @return A help topic.
     */
    public HelpTopic getTopic() {
        return topic;
    }
    
    /**
     * Returns the parent node of this node.
     * 
     * @return The parent node (may be null).
     */
    public HelpTopicNode getParent() {
        return parent;
    }
    
    /**
     * Returns the first sibling node after this one.
     * 
     * @return Next sibling node (may be null).
     */
    public HelpTopicNode getNextSibling() {
        int i = getIndex() + 1;
        return i == 0 || i == parent.children.size() ? null : parent.children.get(i);
    }
    
    /**
     * Returns the first sibling node before this one.
     * 
     * @return Previous sibling node (may be null).
     */
    public HelpTopicNode getPreviousSibling() {
        int i = getIndex() - 1;
        return i < 0 ? null : parent.children.get(i);
    }
    
    /**
     * Appends a child node.
     * 
     * @param node Child node to append.
     */
    public void addChild(HelpTopicNode node) {
        addChild(node, -1);
    }
    
    /**
     * Inserts a child node at the specified position.
     * 
     * @param node Child node to insert.
     * @param index Insertion position (-1 to append).
     */
    public void addChild(HelpTopicNode node, int index) {
        node.detach();
        node.parent = this;
        
        if (index < 0) {
            children.add(node);
        } else {
            children.add(index, node);
        }
    }
    
    /**
     * Removes a child node. If the node is not a current child of this node, the request is
     * ignored.
     * 
     * @param node Child node to remove.
     */
    public void removeChild(HelpTopicNode node) {
        if (node.parent == this) {
            node.detach();
        }
    }
    
    /**
     * Detach a node from its parent.
     */
    private void detach() {
        if (parent != null) {
            parent.children.remove(getIndex());
            parent = null;
        }
    }
}