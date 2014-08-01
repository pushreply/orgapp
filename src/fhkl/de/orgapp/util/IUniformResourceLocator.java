package fhkl.de.orgapp.util;

public interface IUniformResourceLocator
{
	public static final class DomainName
	{
		public static final String SERVER_NAME = "https://pushrply.com/";
	}
	
	public static final class URL
	{
		// TODO The php files
		public static final String URL_COMMENT = DomainName.SERVER_NAME + "example.php";
	}
}