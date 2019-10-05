package com.bemobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bemobile.R
import com.bemobile.databinding.ItemProductListBinding
import com.bemobile.model.Product

class ProductListAdapter(var productArrayList : ArrayList<Product>) :
    RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() , View.OnClickListener {

    private var productListClickListner : ProductListClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        var itemProductListBinding: ItemProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_product_list, parent, false
        )
        itemProductListBinding.layoutProductItem.setOnClickListener(this)
        var productListViewHolder: ProductListViewHolder =
            ProductListViewHolder(itemProductListBinding)
        return productListViewHolder
    }

    override fun getItemCount(): Int {
        return productArrayList.size
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        var product : Product = productArrayList.get(position)
        holder.itemProductListBinding.layoutProductItem.setTag(position)
        holder.Bind(product)
    }

    inner class ProductListViewHolder(var itemProductListBinding: ItemProductListBinding) :
        RecyclerView.ViewHolder(itemProductListBinding.root) {
        fun Bind(product : Product){
            itemProductListBinding.product = product
        }
    }

    override fun onClick(view: View?) {
        var viewId = view?.id
        var position : Int = view?.getTag() as Int
        if(viewId == R.id.layoutProductItem){
            productListClickListner?.ProductTransctaionPage(position)
        }
    }


    fun setProductListClickListner(productListClickListner : ProductListClickListner){
        this.productListClickListner = productListClickListner
    }

    interface ProductListClickListner {
        fun ProductTransctaionPage(position : Int)
    }
}