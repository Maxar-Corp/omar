import org.ossim.omar.security.SecUser
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex


def update()
{
    SecUser.list()?.each { user ->
        if ( user.password != "Authenticated By LDAP" )
        {
            switch ( user.password.size() )
            {
            case 32:
                user.password = new Base64().encodeToString(new Hex().decode(user.password))?.trim() 
                user.save()
                break
            }
        }
    }
}


def reset()
{
    new File("/Users/sbortman/ossim-1.8.10/src/omar/plugins/omar-security-spring/passwords.txt").eachLine { 
        def values = it.split(' ')
        def user = SecUser.get(values[0] as long)
    
        if ( user )
        {
            user.password = values[1]
            user.save()
        }
    }
}    

reset()
update()

return 0