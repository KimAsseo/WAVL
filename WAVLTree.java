/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree.
 * (Haupler, Sen & Tarajan ‘15)
 *
 */


//YOAV SHAMIR, 311332720, yoavshamir1
//KIM ASSEO, 311253827, kimasseo

public class WAVLTree {
	
	public WAVLNode max;
	public WAVLNode min;  
	
	/*the root node of the tree, is null for an empty tree.*/
	public WAVLNode root = null;
	
	/*a single external leaf is used for the entire tree.*/
	public final WAVLNode externalLeaf = new WAVLNode();
	
	public WAVLTree() {
		root = null;
	}
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
    return root==null;
  }

 /**
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  
  public String search(int k) {
	  
	  	WAVLNode found = searchClosestKey(k);
	  	if (found == null) {
	  		return null;
	  	}
	  	else {
	  		if (found.getKey() == k) {
	  			return found.getValue();
	  		} else {
	  			return null;
	  		}
	  	}
  }

  /*returns node with closest key to k in the tree. if not found, returns null.*/
  public WAVLNode searchClosestKey(int k) {
	  
	  WAVLNode curr = root;
	  
	  if (root == null) { //an empty tree.
		  return null;
	  }
	  
	  while (curr.isInnerNode()) {
		  
		  if (k == curr.getKey()) { //checks if the current node has a key matching to the one searched.
			  return curr;
		  }
		  
		  else if (k < curr.getKey()) {
			  if (!curr.getActualLeft().isInnerNode()) {
				  return curr; 
			  }
			  else {
				  curr = curr.getActualLeft(); 
			  }
		  }
		  
		  else {
			  if (!curr.getActualRight().isInnerNode()) {
				  return curr; //CHECK.
			  }
			  else {
				  curr = curr.getActualRight();  
			  }
		  }
	  }
	  return null;
  }
 
  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the WAVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	   if (empty()) {
		   this.root = new WAVLNode(k,i);
		   this.max = root;
		   this.min = root;
		   return 0;
	   }
       WAVLNode possibleParent = searchClosestKey(k);
       int parentKey = possibleParent.getKey();
       if (parentKey == k) { //key already exists.
    	   return -1;
       }
       WAVLNode newNode = new WAVLNode(k,i); //creates the new node

       //updating min, max
       if (k < min.getKey()) {
    	   min = newNode;
       }
       if (k > max.getKey()) {
    	   max = newNode;
       }       
       
       if (k < parentKey) {
    	   possibleParent.setLeft(newNode);
       }
       else {
    	   possibleParent.setRight(newNode);
       }
       newNode.setParent(possibleParent);
       updateTreeSubtreeSize(possibleParent); 
       
       return insertBalance(newNode); //rebalances if needed. 
   }
   
   /*rebalance the tree if needed. counts num of balancing operations*/
   public int insertBalance(WAVLNode node) {
	   
	   if (node.getParent() == null) { //node is root, no balance needed
		   return 0;
	   }
	   WAVLNode parent = node.getParent(); //no 0 rankdiff - no balance needed.
	   if (parent.getRank() != node.getRank()) { 
		   return 0;  
	   }
	   
	   int balanceParent = getBalance(node.getParent()); 
	   if (balanceParent == 1 || balanceParent == -1) {  //promotes node.
		   parent.setRank(parent.getRank() + 1);
		   return (1 + insertBalance(parent));
	   }
	   //rotate options
	   else if (balanceParent == 2) { 

		   int balanceNode = getBalance(node);
		   if (balanceNode == 1) {
			   singleRotate(node);
			   return 2; 
		   }
		   else {
			   doubleRotate(node);
			   return 5;
		   }
	   }
	   else if (balanceParent == -2) {
		   int balanceNode = getBalance(node);
		   if (balanceNode == -1) {
			   singleRotate(node);
			   return 2; 
		   }
		   else {
			   doubleRotate(node);
			   return 5;
		   }
	   }
	   return -1;
   }
   
   /*returns rankdiff from left child - rankdiff from right child*/
   private int getBalance(WAVLNode node) {
	   if (!node.isInnerNode()) {
		   return 0;
	   }
	   int balance = node.getRankDiffLeft()-node.getRankDiffRight();
	   return balance;
   }
   
   public void singleRotate (WAVLNode node) {
	   if (node.isLeftChild()) {
		   rightRotate(node);
	   }
	   else {
		   leftRotate(node);
	   }
   }
   
   public void doubleRotate(WAVLNode node) {
		
	   if (node.isLeftChild()) {
		   WAVLNode right = node.getActualRight();
		   doubleRotateRight(right);
	   }
	   else {
		   WAVLNode left = node.getActualLeft();
		   doubleRotateLeft(left);
	   }
	}
	
	/*double rotate when node is left child*/
	private Void doubleRotateLeft(WAVLNode node) {
		
		rightRotate(node);
		leftRotate(node);
		node.setRank(node.getRank()+1); //fixes rank of node from the rotations.
		return null;
	}
	
	/*double rotate when node is right child*/
	private Void doubleRotateRight(WAVLNode node) {

		leftRotate(node);
		rightRotate(node);
		node.setRank(node.getRank()+1); //fixes rank of node from the rotations.
		return null;
	}
	
	/*single rotate when node is left child*/
	private void rightRotate(WAVLNode node) {
		
		WAVLNode nodeParent = node.getParent();
		WAVLNode grandParent = nodeParent.getParent();
		WAVLNode right = node.getActualRight();
		
		if (nodeParent == root) {
			root = node;
		}
		
		if (grandParent != null) {
			if (grandParent.getActualRight() == nodeParent) {
				grandParent.setRight(node);
			}
			else {
				grandParent.setLeft(node);
			}
		//updating pointers
		node.setParent(grandParent);
		node.setRight(nodeParent); 
		nodeParent.setParent(node);
		nodeParent.setLeft(right);
		
		if (right.isInnerNode()) {
			right.setParent(nodeParent);
		}

		//updating size
		nodeParent.updateSubtreeSize();
		node.updateSubtreeSize();
		if (grandParent != null) {
			grandParent.updateSubtreeSize();
		}
		nodeParent.setRank(nodeParent.getRank()-1);
		}
	}
	
	/*single rotate when node is right child*/
	private void leftRotate(WAVLNode node) {

		WAVLNode parent = node.getParent();
		WAVLNode grandParent = parent.getParent();
		WAVLNode left = node.getActualLeft();
		
		if (parent == root) {
			root = node;
		}
		
		if (grandParent != null) {
			if (grandParent.getActualRight() == parent) {
				grandParent.setRight(node);
			}
			else {
				grandParent.setLeft(node);
			}
		}
		//updating pointers
		node.setParent(grandParent);
		node.setLeft(parent);
		parent.setParent(node);
		parent.setRight(left);
		
		
		if (left.isInnerNode()) {
			left.setParent(parent);
		}
		
		//updating size
		parent.updateSubtreeSize();
		node.updateSubtreeSize();
		if (grandParent != null) {
			grandParent.updateSubtreeSize();
		}
		parent.setRank(parent.getRank()-1);
		}
	
			
   /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
	
   public int delete(int k) {
	   WAVLNode deletionNode = searchClosestKey(k);
	   if (deletionNode.getKey() != k) {
		   return -1; //returns -1 if the key k was not found in the tree.
	   }
	   else {
		   return deleteNode(deletionNode);
	   } 
   }
   
   public int deleteNode(WAVLNode deletionNode) {
	   
	  WAVLNode nodeParent = deletionNode.getParent();
	  
	  //updating min and max if necessary
	  if (deletionNode == min) {
		  min = getSuccessor(min);
	  }
	  if (deletionNode == max) {
		  max = getPredecessor(max);
	  }
	  
	  if (deletionNode.isALeaf()){
		if (nodeParent == null) { // only one node in the tree.
			root = null;
			return 0; // no rebalancing ops. needed.
	  	} else if (deletionNode.isLeftChild()) {
		  nodeParent.setLeft(externalLeaf);  
	  	} else {
		  nodeParent.setRight(externalLeaf); 
	  	}
	  return DeletionRebalanceCount(nodeParent); //rebalance via the node's parent.
	  
   	} else if (deletionNode.getActualRight().isInnerNode() && (!deletionNode.getActualLeft().isInnerNode()))  { 
   	   //unary, only right child.
	   replace(deletionNode, deletionNode.getActualRight());
	   
	   return 1+ DeletionRebalanceCount(nodeParent);
	} else if (deletionNode.getActualLeft().isInnerNode() && (!deletionNode.getActualRight().isInnerNode())) { 
		//unary, only left child.
		replace(deletionNode, deletionNode.getActualLeft());
		return 1+ DeletionRebalanceCount(nodeParent);
		} else { ///the node has two children.  
   			WAVLNode successorNode = getSuccessor(deletionNode);
   			WAVLNode successorNodeParent = successorNode.getParent();
   			replace(deletionNode, successorNode);
   			if (successorNodeParent == deletionNode) {
   				return 1+ DeletionRebalanceCount(successorNode);
   			}else {
   				return 1+ DeletionRebalanceCount(successorNodeParent);
   			}
   		}
   }
	
	/*Replace the nodes nodeA and nodeB*/
	public void replace(WAVLNode nodeA, WAVLNode nodeB) {
		WAVLNode nodeAParent = nodeA.getParent();
		WAVLNode nodeBParent = nodeB.getParent();
		WAVLNode nodeBLeft = nodeB.getActualLeft();
		WAVLNode nodeBRight = nodeB.getActualRight();
		
		if (nodeA.getLeft() != null && nodeA.getRight() != null) { //nodeA has two children.
			nodeB.setRank(nodeA.getRank());
			if (nodeA.getActualLeft() != nodeB){
				nodeB.setLeft(nodeA.getLeft());
				nodeA.getActualLeft().setParent(nodeB);
			}
			if (nodeA.getActualRight() != nodeB) {
				nodeB.setRight(nodeA.getRight());
				nodeA.getActualRight().setParent(nodeB);
			}
			
			if (nodeA.getLeft() != nodeB && nodeA.getRight() != nodeB) {
				if(!nodeBLeft.isInnerNode() && !nodeBRight.isInnerNode()) { //nodeB had no children.
					if (nodeB.isRightChild()) {
						nodeBParent.setRight(externalLeaf);
					} else {
					nodeBParent.setLeft(externalLeaf);
					}
				} else if (!nodeBRight.isInnerNode() && nodeBLeft.isInnerNode()) {
					if(nodeB.isRightChild()) {
						nodeBParent.setRight(nodeBLeft);
						nodeBLeft.setParent(nodeBParent);
					} else {
						nodeBParent.setLeft(nodeBLeft);
						nodeBLeft.setParent(nodeBParent);
					}
				} else if (!nodeBLeft.isInnerNode() && nodeBRight.isInnerNode()) {
					if(nodeB.isRightChild()) {
						nodeBParent.setRight(nodeBRight);
						nodeBRight.setParent(nodeBParent);
					}
					else {
						nodeBParent.setLeft(nodeBRight);
						nodeBRight.setParent(nodeBParent);
					}
				}
			}
		} 
		//connecting nodeB to the parent of nodeA.
		if (nodeAParent != null) {  
			nodeB.setParent(nodeAParent);
			if (nodeA.isLeftChild()) {
				nodeAParent.setLeft(nodeB);
			}
			else {
				nodeAParent.setRight(nodeB);
			} 
		}
		else {  //nodeA is the root of the WAVL tree.
			this.root = nodeB;
			nodeB.setParent(null);
		}
		//demotes if nodeB is now a leaf ////CHECK
		if (nodeB.isALeaf()) {
			nodeB.setRank(0);
		}
	}
	
	/*gets the successor of nodeA*/
	public WAVLNode getSuccessor(WAVLNode nodeA) {
		if (nodeA == null) {
			return null; 
		}
		
		WAVLNode nodeOne = nodeA;
		if (nodeA.getRight() != null) {  //if the node has a right subtree.
			nodeOne = nodeA.getActualRight();
			while (nodeOne.getLeft() != null) { //return the leftmost node in this right subtree of the node.
				nodeOne = nodeOne.getActualLeft();
			}
			return nodeOne;
		}
		
		//finds the successor - the lowest ancestor such that the node is in its left subtree.
		
		if (nodeA.getParent() == null) {
			return null; //nodeA is the root.
		} else if (nodeA.isLeftChild()) { //if node is left child - its parent is the successor.
			return nodeA.getParent();
		} while (nodeOne.isRightChild()) { //climb branch leftword until the first right turn.
			nodeOne = nodeA.getParent(); 
		}
		if (nodeOne.getParent() != null) { //if nodeOne has a parent, the parent is the successor.
			return nodeOne.getParent();
		}
		return null; 
	}
	
	/*gets the predecessor of nodeA*/
	public WAVLNode getPredecessor(WAVLNode nodeA) {
		if (nodeA == null) {
			return null; 
		}
		
		WAVLNode nodeOne = nodeA;
		if (nodeA.getLeft() != null) {  //if the node has a right subtree.
			nodeOne = nodeA.getActualLeft();
			while (nodeOne.getRight() != null) { //return the rightmost node in this right subtree of the node.
				nodeOne = nodeOne.getActualRight();
			}
			return nodeOne;
		}
		
		//finds the predecessor - the lowest ancestor such that the node is in its left subtree.
		
		if (nodeA.getParent() == null) {
			return null; //nodeA is the root.
		} else if (nodeA.isRightChild()) { //if node is right child - its parent is the predecessor.
			return nodeA.getParent();
		} while (nodeOne.isLeftChild()) { //climb branch rightword until the first left turn.
			nodeOne = nodeA.getParent(); 
		}
		if (nodeOne.getParent() != null) { //if nodeOne has a parent, the parent is the predecessor.
			return nodeOne.getParent();
		}
		return null; 
	}
	
	public int DeletionRebalanceCount(WAVLNode node) {
		if (node == null) { //recursion termination after climbing up until the root.
			return 0; 
		
		} else if (node.isALeaf()) { //rank-diff for leaves should be (1,1).
			Integer count = leafDeletionRebalanceCount(node);
			if (count != null) {
				updateTreeSubtreeSize(node);
				return count;
			} 
		}
			
			if(node.getRankDiffLeft() <= 2 && node.getRankDiffRight() <= 2) { 
				//legal rank-diff for WAVL tree node, recursion termination.
				updateTreeSubtreeSize(node);
				return 0;
			}
		if(node.getRankDiffLeft() == 3) {
				int diffRight = node.getRankDiffRight();
				
			if (diffRight == 2) { //node (3,2) - demote once.
				int newRank = node.getRank()-1;
				node.setRank(newRank);
				node.updateSubtreeSize();
				return 1 + DeletionRebalanceCount(node.getParent());
			
			} else if (diffRight == 1) {
				WAVLNode childRight = node.getActualRight();
				
				if (childRight.getRankDiffRight() == 1) { // right node is (1/2,1), rotate left once.
					leftRotate(childRight);
					childRight.setRank(childRight.getRank()+1); //MAYBE = 19/5.
					if (node.isALeaf()) { //additional demotion is required. 
						node.setRank(0);
						return 4; 
					} else {
						return 3;
					}
				} else if (childRight.getRankDiffLeft () == 2 && childRight.getRankDiffRight() == 2) { //right node is (2,2), double demote.
					childRight.setRank(childRight.getRank()-1);
					node.setRank(node.getRank()-1);
					node.updateSubtreeSize();
					return 2 + DeletionRebalanceCount(node.getParent());
				} else if (childRight.getRankDiffRight() == 2) { //right note is (1,2), double rotate. 
					WAVLNode nodeLeft = childRight.getActualLeft();
					doubleRotateLeft(nodeLeft);
					nodeLeft.setRank(nodeLeft.getRank()+1);
					return 5; 
				}
				
			
		} else { // right rank-diff is 3. 
		
			if (node.getRankDiffLeft() == 2) { //demote 1.
				node.setRank(node.getRank()-1);
				node.updateSubtreeSize();
				return 1 + DeletionRebalanceCount(node.getParent());
			} else if (node.getRankDiffLeft() == 1) {
				WAVLNode childLeft = node.getActualLeft();
				
				if (childLeft.getRankDiffRight() == 2 && childLeft.getRankDiffLeft() ==2) { //double demotion.
					childLeft.setRank(childLeft.getRank()-1);
					node.setRank(node.getRank()-1);
					node.updateSubtreeSize();
					return 2 + DeletionRebalanceCount(node.getParent());
			} else if (childLeft.getRankDiffLeft() == 1) { //rotate right once.
				rightRotate(childLeft);
				childLeft.setRank(childLeft.getRank()+1);
				if (node.isALeaf()) {
					node.setRank(0);
					return 4;
				} else {
					return 3;
				}
			} else if (childLeft.getRankDiffLeft() == 2) { //double rotation.
				WAVLNode rightOfLeft = childLeft.getActualRight();
				doubleRotateRight(rightOfLeft);
				rightOfLeft.setRank(rightOfLeft.getRank()+1);
				return 2;
			}
				
		}
	}			
	}
	return 0; 
	}
	
	/*updates subtree size all the way up*/
	public void updateTreeSubtreeSize (WAVLNode node) {
		WAVLNode curr = node; 
		while (curr != null) {
			curr.updateSubtreeSize();
			curr = curr.getParent();
		}
	}
	
	public Integer leafDeletionRebalanceCount(WAVLNode node) {
		if(node.getRankDiffRight() == 2 && node.getRankDiffLeft() == 2) {
			node.setRank(node.getRank()-1);
			return 1 + DeletionRebalanceCount(node.getParent());
		} else if (node.getRankDiffRight() == 1 && node.getRankDiffLeft() == 1) {
			return 0;
		}
		return null;
	}
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if (empty()) {
		   return null;
	   }
	   return this.min.getValue();
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (empty()) {
		   return null;
	   }
	   return this.max.getValue();
	}

   /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
   public int[] keysToArray()
   {
       if (empty()) {
    	   int[] emptyTree = new int[] {};
    	   return emptyTree;
       }
       int[] arr = new int[size()];   
       keysToArrayUpdate(root, arr, 0);
       return arr;       
   }
   
   public int keysToArrayUpdate(WAVLNode node, int[] arr, int location) {
	   if (! node.isInnerNode()) {
		   return location;
	   }
	   location = keysToArrayUpdate(node.getActualLeft(),arr,location);
	   arr[location] = node.getKey();
	   location++;
	   location = keysToArrayUpdate(node.getActualRight(),arr,location);
	   
	   return location;
   }
   /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
   public String[] infoToArray()
   {
       if (empty()) {
    	   String[] emptyTree = new String[] {};
    	   return emptyTree;
       }
       String[] arr = new String[root.getSubtreeSize()];   
       infoToArrayUpdate(root, arr, 0);
       return arr; 
   }

   public int infoToArrayUpdate(WAVLNode node, String[] arr, int location) {
	   if (! node.isInnerNode()) {
		   return location;
	   }
	   location = infoToArrayUpdate(node.getActualLeft(),arr,location);
	   arr[location] = (node.getValue());
	   location = infoToArrayUpdate(node.getActualRight(),arr,++location);
	   
	   return location;
   }
   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    */
   public int size()
   {
           return root.getSubtreeSize(); 
   }
   
     /**
    * public WAVLNode getRoot()
    *
    * Returns the root WAVL node, or null if the tree is empty
    *
    */
   public WAVLNode getRoot()
   {
	   	   if (empty()) {
	   		   return null;
	   	   }
           return this.root;
   }
     /**
    * public int select(int i)
    *
    * Returns the value of the i'th smallest key (return null if tree is empty)
    * Example 1: select(1) returns the value of the node with minimal key 
        * Example 2: select(size()) returns the value of the node with maximal key 
        * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor  
    *
    */   
   public String select(int i)  
   {
	   if (empty()) {
		   return null;
	   }
       WAVLNode curr = this.min;
       
       while (curr.getSubtreeSize()<i) {
    	   if (curr == root) {
    		   break;
    	   }
    	   curr = curr.getParent();
       }
       return select(curr, i).getValue();
   }

   /*recursively returns the value of the i'th smallest key*/
   public WAVLNode select(WAVLNode node, int i) {  
	   int r = node.getActualLeft().getSubtreeSize()+1;

	   if (i == r) {
		   return node;
	   }
	   else if (i < r) {
		   return select(node.getActualLeft(), i);
	   		}
	   else { //i>r
		   return select(node.getActualRight(), i-r);
		   }
	   }
   
   /**
   * public class WAVLNode
   */
  public class WAVLNode{
	  
		private Integer key;
		private String value; 
		private WAVLNode left;
		private WAVLNode right;
		private Integer rank;
		private WAVLNode parent; 
		private int subtreeSize;
		
		/*constructor of external leaves only.*/ 
		public WAVLNode() {
			this.rank = -1;
			this.subtreeSize = 0;
			this.key = null;
		}
		
		/*genral constuctor of wavlnode*/
		public WAVLNode (Integer key, String value) {
			this.right = externalLeaf;
			this.left = externalLeaf;
			this.value = value; 
			this.key = key;
			this.subtreeSize = 1 ;
			this.rank = 0; 
		}
				
		public int getKey() {
				if (isInnerNode()) {
	                return this.key;
				}
				else {
					return -1;
				}
        }
		
		public void setKey(int key) {
			this.key = key;
		}
		
        public String getValue(){
        	if(isInnerNode()) {
        		return this.value;
        	}
        	else {
        		return null;
        	}
        }
        
        public void setValue(String value) {
        	this.value = value;
        }
        
        public WAVLNode getActualRight() {
        	return this.right;
        }
        
        public WAVLNode getActualLeft() {
        	return this.left;
        }
        
        public WAVLNode getLeft()
        {
                return this.getActualLeft() == externalLeaf ? null : this.getActualLeft(); 
        }
        public WAVLNode getRight()
        {
            return this.getActualRight() == externalLeaf ? null : this.getActualRight(); 
        }
        
        public void setRight(WAVLNode node) {
        	this.right = node;
        }
        public void setLeft(WAVLNode node) {
        	this.left = node;
        }
        
        public boolean isInnerNode()
        {
                return this.key != null;
        }
                
        /*sets the subtree size.*/
        public void setSubtreeSize(int subtreeSize) {
        	this.subtreeSize = subtreeSize;
        }
        /*updates the subtree size accodring to both children of the node.*/
        public void updateSubtreeSize() {
        	this.setSubtreeSize(getActualRight().getSubtreeSize() + getActualLeft().getSubtreeSize() + 1);
        }
        
        public int getSubtreeSize()
        {
                return subtreeSize;
        }
        
        /*returns the rank of the wavl node.*/
        public int getRank()
        {
        		return this.rank;
        }
        
        /*sets the rank of the wavl node.*/
        public void setRank(int rank) {
        	this.rank = rank;
        }
                
        /*gets the parent of the wavl node.*/
        public WAVLNode getParent()
        {
                return this.parent;
        }
        
        /*sets the parent of the wavl node.*/
        public void setParent(WAVLNode parent) {
        	this.parent = parent;
        }
        
        /*returns true if the wavl node is an actual leaf.*/
        public boolean isALeaf () {
        	return ((this.getLeft() == null) && (this.getRight() == null));
        }
        
        /*returns true if the node is a left child of its parent.*/
        public boolean isLeftChild() {
        	return((this.getParent() != null) && (this.getParent().getLeft() == this));
        }
        
        /*returns true if the node is a right child of its parent.*/
        
        public boolean isRightChild() {
        	return((this.getParent() != null) && (this.getParent().getRight() == this));
        }
        
        /*returns the rank difference with the left node.*/
        public int getRankDiffLeft() {
        	return this.getRank() - this.getActualLeft().getRank(); //check this.
        }

        /*returns the rank difference with the right node.*/
        public int getRankDiffRight() {
        	return this.getRank() - this.getActualRight().getRank();
        }
        
  }
  		
}