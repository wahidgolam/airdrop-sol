package com.zingit.user;

import com.google.firebase.Timestamp;
import com.zingit.user.model.Coupon;

import java.util.ArrayList;
import java.util.Date;

public class Cart {
    String cartOutletID;
    private long cartTotal;
    private ArrayList<CartItem> cartItems;
    Coupon coupon = null;

//<--------CONSTRUCTORS---------->

    public Cart() {
        cartItems = new ArrayList<>();
    }

    public Cart(String cartOutletID) {
        this.cartOutletID = cartOutletID;
        cartItems = new ArrayList<>();
    }

    //<<--------CART HANDLING FUNCTIONS-------->>

    public int findIfItemPresent(Item item){
        for(int i = 0; i<cartItems.size(); i++){
            if(cartItems.get(i).getItem().getId().equals(item.getId())){
                return i;
            }
        }
        return -1; //returns position if item is present in the cart, -1 if not
    }
    public int getTotalCount(){
        int count = 0;
        for(int i = 0; i<cartItems.size();i++){
            count+= cartItems.get(i).getQuantity();
        }
        return count;
    }
    public ArrayList<Item> returnItemList(){
        ArrayList<Item> itemList = new ArrayList<>();
        for(int i = 0; i<cartItems.size(); i++){
            itemList.add(cartItems.get(i).getItem());
        }
        return itemList;
    }
    public int findQuantity(Item item){
        int pos = findIfItemPresent(item);
        if(pos==-1){
            return 0;
        }
        else {
            return cartItems.get(pos).getQuantity();
        }
    }
    public int verifyOutletIDMatch(Item item){
        if(item.getOutletID().equals(cartOutletID)){
            return 1;
        }
        else{
            return 0;
        }//returns 1 if cartID valid, 0 if invalid
    }
    public int updateItem(Item item, int operation){
        int item_pos = findIfItemPresent(item);
        if(verifyOutletIDMatch(item)==1){
            if(item_pos!=-1){
                if(operation == 1){
                    int quantity = cartItems.get(item_pos).getQuantity();
                    int maxAllowed = cartItems.get(item_pos).getItem().getMaxAllowed();
                    if(quantity<maxAllowed){
                        cartItems.get(item_pos).setQuantity(++quantity);
                        updateCartTotal();
                        return 1;//item quantity incremented successfully
                    }
                    else{
                        updateCartTotal();
                        return -1;//item quantity has reached maxLimit
                    }
                }
                else if(operation == -1){
                    int quantity = cartItems.get(item_pos).getQuantity();
                    int maxAllowed = cartItems.get(item_pos).getQuantity();
                    if(quantity>1){
                        cartItems.get(item_pos).setQuantity(--quantity);
                        updateCartTotal();
                        return 2;//item quantity decremented successfully
                    }
                    else if(quantity==1){
                        cartItems.remove(item_pos);
                        updateCartTotal();
                        return 3;//item removed
                    }
                    else{
                        cartItems.remove(item_pos);
                        updateCartTotal();
                        return 4;//item existing without quantity, removed
                    }
                }
                else{
                    updateCartTotal();
                    return -2;//operation invalid
                }
            }
            else {
                if(operation==1) {
                    cartItems.add(new CartItem(item, 1));
                    updateCartTotal();
                    return 5;//new item added to cart
                }
                else if(operation==-1) {
                    updateCartTotal();
                    return -3;
                }//decrement request on item that does not exist
                else {
                    updateCartTotal();
                    return -2;//operation invalid
                }
            }
        }
        else {
            return 0;//outletIDNotMatched
        }//-2,-1,0,1,2,3,4,5 status codes
    }
    public void updateCartTotal(){
        int totalPrice=0;
        for(int i= 0; i<cartItems.size(); i++){
            totalPrice += cartItems.get(i).getPrice()*cartItems.get(i).getQuantity();
        }
        cartTotal = totalPrice;
    }
    public void flushCart(){
        cartOutletID = "";
        cartItems = new ArrayList<>();
        cartTotal = 0;
    }
    public void flushCart(String cartOutletID,Item item){
        this.cartOutletID = cartOutletID;
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem(item, 1));
        updateCartTotal();
    }

    /*------------ORDER VERIFICATION FUNCTIONS-------------*/
    public int verifyOutletUniformity(){
        for(int i= 0; i<cartItems.size(); i++){
            if(!cartOutletID.equals(cartItems.get(i).getItem().getOutletID())){
                return 0;
            }
        }
        return 1;
    }

    public int verifyCartTotal(){
        int cartTotalLocal = 0;
        for(int i= 0; i<cartItems.size(); i++){
            cartTotalLocal += cartItems.get(i).getPrice() * cartItems.get(i).getQuantity();
        }
        if(cartTotalLocal == cartTotal)
            return 1;
        else
            return 0;
    }

    public int verifyBeforePlacingOrder(){
        return verifyOutletUniformity()&verifyCartTotal();
    }
    public int removeNotAvailableItems(ArrayList<Item> items){
        for(int i=0; i<items.size(); i++){
            Item item = items.get(i);
            int pos = findIfItemPresent(item);
            if(pos!=-1){
                if(!item.isAvailableOrNot()){
                    cartItems.remove(pos);
                    updateCartTotal();
                    return pos;
                }
            }
        }
        return -1;
    }
    public ArrayList<Order> createOrdersFromCart(){
        if(verifyBeforePlacingOrder()==1) {
            Date date = new Date();
            Timestamp placedTime = new Timestamp(date);
            ArrayList<Order> localOrders = new ArrayList<>();
            for (int i = 0; i < cartItems.size(); i++) {
                Item item = cartItems.get(i).getItem();
                String id = item.getId();
                int quantity = cartItems.get(i).getQuantity();

                Order order = new Order(getCartOutletID(), Dataholder.studentUser.getUserID(), placedTime, 1, id, quantity, item.getPrice() * quantity, item.getName(), item.getItemImage());
                localOrders.add(order);
            }
            return localOrders;
        }
        else{
            return null;
        }
    }

    /*------------GETTER FUNCTIONS-------------*/

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public String getCartOutletID() {
        return cartOutletID;
    }

    public long getCartTotal() {
        return cartTotal;
    }

    /*-----------COUPON CODE FUNCTIONS----------*/


    public double getDiscountedCartTotal() {
        if(coupon!=null){
            if(coupon.getApplyOn()==1){
                double cumulativeDiscount = 0;
                for(int i=0; i<getCartItems().size();i++){
                    if(coupon.getApplyID().contains(getCartItems().get(i).getItem().getId())){
                        if(coupon.getDiscountType()==0){
                            //flat
                            cumulativeDiscount += (double)coupon.getDiscount()*findQuantity(getCartItems().get(i).getItem())*1.0;
                        }else{
                            //percentage
                            cumulativeDiscount += (double) getCartItems().get(i).getItem().getPrice()*coupon.getDiscount()*findQuantity(getCartItems().get(i).getItem())*1.0;
                        }
                    }
                }
                return getCartTotal()-cumulativeDiscount;
            }
            else{
                if(coupon.getDiscountType()==0){
                    //flat
                    return (cartTotal-coupon.getDiscount());
                }else{
                    //percentage
                    return (double)(cartTotal*(1- (coupon.getDiscount()/100.0)));
                }
            }
        }else{
            return cartTotal;
        }
    }
    public void dismissCoupon(){
        coupon=null;
    }
    public boolean isCouponPresent(){
        return (coupon==null)?false:true;
    }

    public Coupon getCoupon() {
        return coupon;
    }
    public double getSavings(){
        return (getCartTotal()-getDiscountedCartTotal());
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
}