<?php

namespace App\Http\Controllers;

use App\Http\Resources\CategoryResource;
use App\Models\Category;
use Illuminate\Http\Request;

class CategoryController extends Controller
{
    /**
     * List all categories.
     */
    public function index(Request $request)
    {
        $categories = Category::all();
        
        return CategoryResource::collection($categories);
    }

    /**
     * Get category details.
     */
    public function show(Request $request, $id)
    {
        $category = Category::findOrFail($id);
        
        return new CategoryResource($category);
    }
}
