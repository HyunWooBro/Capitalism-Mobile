package project.game.corporation;

import java.util.List;

import project.game.building.Building;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.stock.Sales;
import project.game.product.Product;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductDescription;

import core.math.MathUtils;

public class PublicCorporation extends Corporation {
	
	public PublicCorporation() {
		mFinancialData = new FinancialData();
	}

	public void InitPort() {

		Building building = mBuildingList.get(0);

		ProductDescription description;
		Sales sales;

		List<ProductDescription> descList = ProductManager.getInstance()
				.getImportablePDescList();

		MathUtils.beginRandomUnique(0, 3);
		int rand;
		
		Product product;

		rand = MathUtils.getRandomUnique();
		description = ProductManager.getInstance().getPDescByCode(descList.get(rand).code);
		sales = (Sales) (building.getDepartmentManager().getDepartments()[0] = building
				.newDepartment(0, DepartmentType.SALES));
		product = ProductManager.getInstance().newProduct(description);
		product.producer = this;
		//product.mSupplier = this;
		product.quality = MathUtils.random(50, 70);
		sales.confirmProduct(product);
		
		ProductManager.getInstance().addProduct(product, this);
		// sales.m_inverntory = sales.m_maxinverntory;
		//sales.mLinkedProduct.mProductGroup.brandAwareness = MathUtils.random(0, 50);
		
		

		rand = MathUtils.getRandomUnique();
		description = ProductManager.getInstance().getPDescByCode(descList.get(rand).code);
		sales = (Sales) (building.getDepartmentManager().getDepartments()[1] = building
				.newDepartment(1, DepartmentType.SALES));
		product = ProductManager.getInstance().newProduct(description);
		product.producer = this;
		//product.mSupplier = this;
		product.quality = MathUtils.random(50, 70);
		sales.confirmProduct(product);
		
		ProductManager.getInstance().addProduct(product, this);
		// sales.m_inverntory = sales.m_maxinverntory;
		//sales.mLinkedProduct.mProductGroup.brandAwareness = MathUtils.random(0, 50);
		
		

		rand = MathUtils.getRandomUnique();
		description = ProductManager.getInstance().getPDescByCode(descList.get(rand).code);
		sales = (Sales) (building.getDepartmentManager().getDepartments()[3] = building
				.newDepartment(3, DepartmentType.SALES));
		product = ProductManager.getInstance().newProduct(description);
		product.producer = this;
		//product.mSupplier = this;
		product.quality = MathUtils.random(50, 70);
		sales.confirmProduct(product);
		
		ProductManager.getInstance().addProduct(product, this);
		// sales.m_inverntory = sales.m_maxinverntory;
		//sales.mLinkedProduct.mProductGroup.brandAwareness = MathUtils.random(0, 50);
		
		

		rand = MathUtils.getRandomUnique();
		description = ProductManager.getInstance().getPDescByCode(descList.get(rand).code);
		sales = (Sales) (building.getDepartmentManager().getDepartments()[4] = building
				.newDepartment(4, DepartmentType.SALES));
		product = ProductManager.getInstance().newProduct(description);
		product.producer = this;
		//product.mSupplier = this;
		product.quality = MathUtils.random(50, 70);
		sales.confirmProduct(product);
		
		ProductManager.getInstance().addProduct(product, this);
		// sales.m_inverntory = sales.m_maxinverntory;
		//sales.mLinkedProduct.mProductGroup.brandAwareness = MathUtils.random(0, 50);
	}

}
