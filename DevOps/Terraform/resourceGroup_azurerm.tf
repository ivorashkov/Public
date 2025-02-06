terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.75.0 "
    }
  }
}

# Configure the Microsoft Azure provider
provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "resourcegroup" {
  name     = "resourcegroupivo"
  location = "West Europe"
}