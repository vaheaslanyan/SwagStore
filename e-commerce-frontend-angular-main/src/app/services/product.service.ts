import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Product } from '../models/product';
import { environment } from 'src/environments/environment';

interface Cart {
  cartCount: number;
  products: {
    product: Product;
    quantity: number;
  }[];
  totalPrice: number;
}

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private productUrl: string = '/api/product';

  private _cart = new BehaviorSubject<Cart>({
    cartCount: 0,
    products: [],
    totalPrice: 0.0,
  });

  private _cart$ = this._cart.asObservable();

  getCart(): Observable<Cart> {
    return this._cart$;
  }

  setCart(latestValue: Cart) {
    return this._cart.next(latestValue);
  }

  constructor(private http: HttpClient) {}

  public getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(environment.baseUrl + this.productUrl, {
      headers: environment.headers,
      withCredentials: environment.withCredentials,
    });
  }

  public getSingleProduct(id: number): Observable<Product> {
    return this.http.get<Product>(
      environment.baseUrl + this.productUrl + '/' + id,
      {
        withCredentials: environment.withCredentials,
      }
    );
  }

  public updateProduct(product: Product) {
    return this.http.put(environment.baseUrl + this.productUrl, product, {
      withCredentials: environment.withCredentials,
    });
  }

  public uploadProductImage(product_id: number, formData: FormData) {
    return this.http.put<any>(
      environment.baseUrl + '/api/product/image/' + product_id,
      formData,
      {
        withCredentials: true,
      }
    );
  }

  public purchase(
    products: { id: number; quantity: number }[]
  ): Observable<any> {
    const payload = JSON.stringify(products);
    return this.http.patch<any>(
      environment.baseUrl + this.productUrl,
      payload,
      {
        headers: environment.headers,
        withCredentials: environment.withCredentials,
      }
    );
  }
}
