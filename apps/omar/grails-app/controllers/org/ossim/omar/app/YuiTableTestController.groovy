package org.ossim.omar.app

import grails.converters.JSON
import org.ossim.omar.raster.RasterEntryQuery

class YuiTableTestController
{
  def rasterEntrySearchService


  static final imageColumnDefs = [
      [key: 'id', label: 'Id', sortable: true, resizeable: true],
      [key: 'acquisitionDate', label: 'Date', formatter: 'date', sortable: true, resizeable: true],
      [key: 'minLon', label: 'Min Lon', sortable: false, resizeable: true],
      [key: 'minLat', label: 'Min Lat', sortable: false, resizeable: true],
      [key: 'maxLon', label: 'Max Lon', sortable: false, resizeable: true],
      [key: 'maxLat', label: 'Max Lat', sortable: false, resizeable: true],
      [key: 'width', label: 'Width', formatter: 'number', sortable: true, resizeable: true],
      [key: 'height', label: 'Height', formatter: 'number', sortable: true, resizeable: true],
      [key: 'bitDepth', label: 'Bit Depth', formatter: 'number', sortable: true, resizeable: true],
      [key: 'dataType', label: 'Data Type', sortable: true, resizeable: true],
      [key: 'numberOfBands', label: 'Bands', formatter: 'number', sortable: true, resizeable: true]
  ]

  static final metadataColumnDefs = [
      [key: 'id', label: 'Id', sortable: true, resizeable: true],
      [key: 'acquisitionDate', label: 'Date', formatter: 'date', sortable: true, resizeable: true],
      [key: 'fileType', label: 'Type', sortable: false, resizeable: true],
      [key: 'className', label: 'Class', sortable: false, resizeable: true],
      [key: 'mission', label: 'Mission', sortable: false, resizeable: true],
      [key: 'country', label: 'Country', sortable: false, resizeable: true],
      [key: 'targetId', label: 'Target Id', sortable: false, resizeable: true],
      [key: 'sensor', label: 'Sensor', sortable: false, resizeable: true],
      [key: 'imageId', label: 'Image Id', sortable: false, resizeable: true]
  ]

  static final fileColumnDefs = [
      [key: 'id', label: 'Id', sortable: true, resizeable: true],
      [key: 'mainFile', label: 'Main File', sortable: false, resizeable: true],
      [key: 'entryId', label: 'Entry Id', sortable: true, resizeable: true]
  ]


  static final linkColumnDefs = [
      [key: 'id', label: 'Id', sortable: true, resizeable: true]
  ]

  def index = { }

  def getData = {
    println params
    render contextType: 'application/json', text: """
{"recordsReturned":25,"totalRecords":1397,"startIndex":0,"sort":"id","dir":"asc","pageSize":25,"records":[{"id":"0","name":"xmlqoyzgmykrphvyiz","date":"13-Sep-2002","price":"8370","number":"8056","address":"qdfbc","company":"taufrid","desc":"pppzhfhcdqcvbirw","age":"5512","title":"zticbcd","phone":"hvdkltabshgakjqmfrvxo","email":"eodnqepua","zip":"eodnqepua","country":"pdibxicpqipbsgnxyjumsza"},{"id":"1","name":"rbdmbabficcre","date":"10-Sep-2004","price":"3075","number":"3627","address":"oxcm","company":"xyzwzv","desc":"rwndyoedxh","age":"2134","title":"lxxyfgdtdffjce","phone":"zeejvbwy","email":"ldcikhxwfuulaxeedkogpxftb","zip":"ldcikhxwfuulaxeedkogpxftb","country":"pcmobxrdfclcyrx"},{"id":"2","name":"yr","date":"04-Mar-2007","price":"7129","number":"6614","address":"i","company":"gcpvrshftfxxlz","desc":"nyalrdjjl","age":"4728","title":"ddfl","phone":"mnhifzqltvirgiaug","email":"f","zip":"f","country":"epipbmtfsfxetenyedjxzsog"},{"id":"3","name":"bhqggvwolybfdtk","date":"26-Dec-2000","price":"1867","number":"4288","address":"jo","company":"goevufkvmbct","desc":"zhixinabyazbfleozrvovr","age":"3423","title":"b","phone":"odhh","email":"g","zip":"g","country":"idxvdztezvkkaz"},{"id":"4","name":"uynlhonmcqtjqzyzd","date":"23-Nov-2002","price":"8497","number":"5846","address":"vlwglvrcqqqc","company":"epkhgeqxdpwhlsohhadsxkd","desc":"bgjrxlpbbzihdzfhpcp","age":"417","title":"ejbfmucwyvyefpcqfdse","phone":"zimfqbhfccjl","email":"oyy","zip":"oyy","country":"vwntbsjdiohattacg"},{"id":"5","name":"inylpixtxvrorobkpt","date":"20-Oct-2000","price":"3551","number":"9863","address":"cte","company":"akyiy","desc":"tofqicmaqdosodljvosvrv","age":"5844","title":"apgyxfvrtahccuctxqlmtx","phone":"plnldiaaiphhnmcegcmif","email":"kyeakq","zip":"kyeakq","country":"hlktyvxhwyqmfxzrzexc"},{"id":"6","name":"iypgc","date":"19-Aug-2006","price":"4301","number":"9732","address":"vtdr","company":"knpazuyzlxdjauois","desc":"pvvg","age":"5491","title":"gsczwt","phone":"ggfhgaffldjvedkn","email":"ocskjgrpztovnfctlkalqf","zip":"ocskjgrpztovnfctlkalqf","country":"uosysclprohbxotnnmbizh"},{"id":"7","name":"svtdlzafosegupwlddn","date":"20-Dec-2001","price":"5616","number":"2005","address":"qosxzmqugtgfultmpah","company":"wtjag","desc":"rnzxgqlynkl","age":"1577","title":"rxlw","phone":"wpemqmsmfcmmneznbf","email":"mer","zip":"mer","country":"pvbgtmdljtqwjdpvirhvfm"},{"id":"8","name":"navzfnwuiybclenvxe","date":"08-Sep-2002","price":"2842","number":"1098","address":"ykldwgwkhrkmfgho","company":"iqqne","desc":"lidspkvfjgq","age":"5341","title":"xk","phone":"ecxrheyvjhlzu","email":"kfxoxnztsjzjxcg","zip":"kfxoxnztsjzjxcg","country":"pljmdqq"},{"id":"9","name":"zixzdnjilygy","date":"01-Nov-2006","price":"2605","number":"9735","address":"d","company":"h","desc":"qtudwltyxth","age":"8063","title":"lictuaasyakfalibst","phone":"movjzpiwjqrbbaevuf","email":"ndwxixiryblroznxzcgvmw","zip":"ndwxixiryblroznxzcgvmw","country":"nxnsshxoualrj"},{"id":"10","name":"a","date":"02-Mar-2004","price":"9659","number":"5969","address":"mnkptgbptpm","company":"hfoeti","desc":"faona","age":"5707","title":"caurlgfwwycxnwmadufrz","phone":"aeeasrbhneiivupaqlztimpvm","email":"pskorksvllnntbrcjnw","zip":"pskorksvllnntbrcjnw","country":"onjngrawnnpcfarwlksxwgkp"},{"id":"11","name":"rq","date":"16-Jul-2004","price":"1434","number":"565","address":"riprfcevfjvwgggydceoslq","company":"cfyqjaoaie","desc":"nhvimfejlkpkospdl","age":"677","title":"vdzuujuijcmbquxygdc","phone":"pnfzcypfjrzfvzz","email":"ityswlumfrlmvoc","zip":"ityswlumfrlmvoc","country":"cikegzkqrk"},{"id":"12","name":"jucsobkknext","date":"09-Nov-2001","price":"6827","number":"8546","address":"bypmdwl","company":"mdyiqidsarulch","desc":"zamhgedogcdsfze","age":"7409","title":"hruxaxqapllr","phone":"bqtoyascoyesqjsvcec","email":"yauwrvlcgdviupwspoue","zip":"yauwrvlcgdviupwspoue","country":"zwejozmscgrdb"},{"id":"13","name":"wzxdcsmwiipyxkcljyqs","date":"17-Jun-2001","price":"2073","number":"7075","address":"wvmkqjknnng","company":"j","desc":"jynhipssoikayp","age":"2377","title":"admnocxyqllwlvlu","phone":"yccouucdfdbujsvmeia","email":"gzyrkvd","zip":"gzyrkvd","country":"gxzfzc"},{"id":"14","name":"xwxdazxjstwwbxeiwczh","date":"03-Nov-2004","price":"1800","number":"235","address":"kapevnbtqbsol","company":"hhhjemrbpq","desc":"ntvruicu","age":"9596","title":"pmiickb","phone":"vmydtiigzjvqrijna","email":"wcy","zip":"wcy","country":"johsxkcycykbbejklitgy"},{"id":"15","name":"iyqlvtjqcyyvvjy","date":"13-Sep-2007","price":"1782","number":"5005","address":"jwiuecbdnrlmhwiagzdexzzh","company":"ttuygshcbcheeksvvedsnez","desc":"iekhesfxlzws","age":"7184","title":"vug","phone":"yl","email":"qgivkbipavye","zip":"qgivkbipavye","country":"gjxlg"},{"id":"16","name":"dbegwynzxym","date":"06-Jun-2007","price":"3561","number":"6233","address":"rfwmdbsjkpvryguakbwjow","company":"niusdd","desc":"zvowisxac","age":"3143","title":"xzpeuppvmykifyrz","phone":"ucztyopghnhjvxhv","email":"lpbblozvwftn","zip":"lpbblozvwftn","country":"kihk"},{"id":"17","name":"zsmgf","date":"17-Sep-2000","price":"6671","number":"8788","address":"dimfjxujtqonetynbiphih","company":"onneoealijxotuicozr","desc":"e","age":"4195","title":"rmigtqomebaiqejbntycmtkph","phone":"gjorigdrnw","email":"bjmdjvt","zip":"bjmdjvt","country":"fubytdlmoauzhd"},{"id":"18","name":"mucdhyljixmrtgfybhxuljga","date":"02-Apr-2008","price":"6544","number":"1674","address":"pqhrupqfyydlq","company":"rvvsdtnoctomuodzbtpilj","desc":"bpwashqpzmkspefdgzstugwt","age":"3134","title":"jratpcildbttqsf","phone":"l","email":"fqymqrfkxcegsnxthmwq","zip":"fqymqrfkxcegsnxthmwq","country":"zrrsikykvsqmrcciimglqme"},{"id":"19","name":"xlvtcttuk","date":"04-Jun-2007","price":"3963","number":"5126","address":"ddcugedprjahwelg","company":"xb","desc":"zvptgbwblkspnujuznjqxj","age":"9302","title":"okzqhbmhxcbedyfpiye","phone":"toqsczjzihtwrsnzuzgsci","email":"fgcvoaaltpbmravqjdjgvc","zip":"fgcvoaaltpbmravqjdjgvc","country":"uxtbpvklbqnxfnxqhmr"},{"id":"20","name":"rpub","date":"06-Sep-2002","price":"5542","number":"2795","address":"j","company":"uktp","desc":"erlsoqglhnyzhcrwxspc","age":"9766","title":"jano","phone":"yhjsmbefpvlbcyabfcsc","email":"l","zip":"l","country":"ckwmkjaehioavqfafa"},{"id":"21","name":"cjhepkx","date":"12-Aug-2003","price":"3672","number":"5207","address":"fadpaixixyendesdpzg","company":"hlorifbrppkvpokqxizugdhjh","desc":"n","age":"8702","title":"t","phone":"hekynqaffpqafflvctuwa","email":"ejeexbfqwmvhljxmpccfdhk","zip":"ejeexbfqwmvhljxmpccfdhk","country":"cmiwjjunsyspzxg"},{"id":"22","name":"cdvlaiadkg","date":"19-Mar-2005","price":"8373","number":"7612","address":"eqnn","company":"agjtwiqdfa","desc":"jwrje","age":"7004","title":"oytgppzuuqhib","phone":"okblsroy","email":"uhplrudejcypsopmif","zip":"uhplrudejcypsopmif","country":"qgcfqdqjvfiozpdkgxo"},{"id":"23","name":"qjvjxl","date":"07-Oct-2005","price":"5878","number":"8749","address":"rcousxqxgexvihcfwnmnxiw","company":"trbkgyhyavtstjqzonvwv","desc":"bslofjwbeqsfazdhxedqwwa","age":"5234","title":"obskxqmpbbvlywpopvppzw","phone":"dadaxenuspmd","email":"cqceryppuedk","zip":"cqceryppuedk","country":"t"},{"id":"24","name":"pmcqpcnuqinfuqswgvaytpnou","date":"27-Aug-2007","price":"4192","number":"9188","address":"ajwrzzftpngvhxoeeke","company":"du","desc":"soirjtpduzzlyyrs","age":"5436","title":"zjmw","phone":"qbhuelpueedvnxkqrjqdiou","email":"c","zip":"c","country":"amkxkbzsvdelyipbecyptqzj"}]}
    """
  }

  static defaultAction = "foo"

  def foo = {

    def queryParams = new RasterEntryQuery()


    println "params: ${params}"

    bindData(queryParams, params)

    if ( params.startDate )
    {
      queryParams.startDate = Date.parse("MM/dd/yyyy HH:mm:ss", params.startDate)
    }

    if ( params.endDate )
    {
      queryParams.endDate = Date.parse("MM/dd/yyyy HH:mm:ss", params.endDate)
    }

    println "queryParams: ${queryParams?.toMap2()}"

    //def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)

    [
        blah: [foo: "one", bar: "two"],
        imageColumnDefs: imageColumnDefs,
        metadataColumnDefs: metadataColumnDefs,
        fileColumnDefs: fileColumnDefs,
        linkColumnDefs: linkColumnDefs,
        queryParams: queryParams
    ]
  }

  def dataAsJSON = {
    println "start: dataAsJSON ${new Date()} ${params}"

    def list = []

    def queryParams = new RasterEntryQuery()

    println "params: ${params}"

    bindData(queryParams, params)

    if ( params.startDate )
    {
      queryParams.startDate = Date.parse("MM/dd/yyyy HH:mm:ss", params.startDate)
    }

    if ( params.endDate )
    {
      queryParams.endDate = Date.parse("MM/dd/yyyy HH:mm:ss", params.endDate)
    }

    println "queryParams: ${queryParams?.toMap2()}"

    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)

    response.setHeader("Cache-Control", "no-store")

    rasterEntries.each {rasterEntry ->
      def bounds = rasterEntry.groundGeom.bounds

      list << [
          id: rasterEntry.id,
          minLon: bounds.minLon,
          minLat: bounds.minLat,
          maxLon: bounds.maxLon,
          maxLat: bounds.maxLat,
          bitDepth: rasterEntry.bitDepth,
          numberOfBands: rasterEntry.numberOfBands,
          dataType: rasterEntry.dataType,
          width: rasterEntry.width,
          height: rasterEntry.height,
          acquisitionDate: rasterEntry.acquisitionDate ?: "",
//          fileType: rasterEntry.metadataTags.find {tag -> tag.name == "file_type" }?.value ?: "",
//          className: rasterEntry.metadataTags.find {tag -> tag.name == "class_name" }?.value ?: "",
//          mission: rasterEntry.metadataTags.find {tag -> tag.name == "mission" }?.value ?: "",
//          sensor: rasterEntry.metadataTags.find {tag -> tag.name == "sensor" }?.value ?: "",
//          targetId: rasterEntry.metadataTags.find {tag -> tag.name == "targetId" }?.value ?: "",
//          country: rasterEntry.metadataTags.find {tag -> tag.name == "country" }?.value ?: "",
//          imageId: rasterEntry.metadataTags.find {tag -> tag.name == "imageId" }?.value ?: "",
          entryId: rasterEntry.entryId,
          mainFile: rasterEntry.mainFile.name
      ]
    }

    def data = [
        totalRecords: rasterEntries?.totalCount,
        results: list
    ]

    render data as JSON

    println "end: dataAsJSON ${new Date()}"

  }
}
